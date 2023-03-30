package edu.uaux.pheart.measure

import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.PointF
import android.util.Size
import android.view.Surface
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
    private val callback: Callback,
) : ImageAnalysis.Analyzer, Consumer<MlKitAnalyzer.Result> {

    private val faceDetector = FaceDetectionHelper.buildFaceDetector()
    private val mlKitAnalyzer = FaceDetectionHelper.buildMlKitAnalyzer(faceDetector, callbackExecutor, this)
    private val backgroundExecutor: Executor = Executors.newSingleThreadExecutor()

    val useCase: ImageAnalysis = ImageAnalysis.Builder()
        .setTargetRotation(Surface.ROTATION_0)
        .build()
        .also { analysis ->
            analysis.setAnalyzer(backgroundExecutor, this)
        }

    private var lastFaces: List<Face> = emptyList()

    override fun accept(result: MlKitAnalyzer.Result) {
        // Log throwable if not-null
        Timber.e(result.getThrowable(faceDetector))

        // Send results to callback
        val faces = result.getValue(faceDetector)
        lastFaces = faces.orEmpty()
        if (!faces.isNullOrEmpty()) {
            callback.onFacesDetected(faces)
        }
    }

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

    override fun getDefaultTargetResolution(): Size = mlKitAnalyzer.defaultTargetResolution
    override fun getTargetCoordinateSystem(): Int = mlKitAnalyzer.targetCoordinateSystem
    override fun updateTransform(matrix: Matrix?) {
        mlKitAnalyzer.updateTransform(matrix)
    }

    interface Callback {
        fun onFacesDetected(faces: List<Face>)
        fun onMeasurementTaken(averageLuminance: Float)
        fun onMeasurementCancelled()

        fun onShowPoints(points: List<PointF>, imageWidth: Int, imageHeight: Int) = Unit
    }
}