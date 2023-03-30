package edu.uaux.pheart.measure

import android.graphics.ImageFormat
import android.graphics.Matrix
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.core.util.Consumer
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import timber.log.Timber
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class FacialHeartRateAnalyzer(
    private val callbackExecutor: Executor,
    private val callback: MeasurementCallback,
) : ImageAnalysis.Analyzer, Consumer<MlKitAnalyzer.Result> {

    private val faceDetector = FaceDetectionHelper.buildFaceDetector()
    private val mlKitAnalyzer = FaceDetectionHelper.buildMlKitAnalyzer(faceDetector, callbackExecutor, this)
    private val backgroundExecutor: Executor = Executors.newSingleThreadExecutor()

    val useCase: ImageAnalysis = ImageAnalysis.Builder().build().also { analysis ->
        analysis.setAnalyzer(backgroundExecutor, this)
    }

    private var lastFaces: List<Face> = emptyList()

    override fun analyze(image: ImageProxy) {
        require(image.format == ImageFormat.YUV_420_888)
        val yPlane = image.planes.first()
        val face = lastFaces.singleOrNull()
        if (face != null) {
            val facePoints = face.getContour(FaceContour.FACE)?.points.orEmpty()

            // TODO: locate face and extract average luminance

            callbackExecutor.execute {
                callback.onShowPoints(facePoints, image.width, image.height)
            }
        } else {
            callbackExecutor.execute {
                callback.onMeasurementCancelled()
            }
        }

        mlKitAnalyzer.analyze(image)
    }

    /**
     * Process the result of the ML Kit face detector.
     */
    override fun accept(result: MlKitAnalyzer.Result) {
        lastFaces = result.getValue(faceDetector).orEmpty()
        Timber.e(result.getThrowable(faceDetector)) // no-op if null
    }

    override fun getDefaultTargetResolution(): Size = mlKitAnalyzer.defaultTargetResolution
    override fun getTargetCoordinateSystem(): Int = mlKitAnalyzer.targetCoordinateSystem
    override fun updateTransform(matrix: Matrix?) {
        mlKitAnalyzer.updateTransform(matrix)
    }
}