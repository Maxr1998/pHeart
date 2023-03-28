package edu.uaux.pheart.measure

import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import timber.log.Timber
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceDetectionHelper(
    callbackExecutor: Executor,
    private val callback: Callback,
) {
    private val imageAnalyzerExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }

    private val faceDetector = FaceDetection.getClient(buildFaceDetectorOptions())
    private val mlKitAnalyzer = MlKitAnalyzer(
        listOf(faceDetector),
        ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL,
        callbackExecutor,
    ) { result ->
        // Log throwable if not-null
        Timber.e(result.getThrowable(faceDetector))

        // Send results to callback
        val faces = result.getValue(faceDetector)
        if (!faces.isNullOrEmpty()) {
            callback.onFacesDetected(faces)
        }
    }

    val useCase: ImageAnalysis = ImageAnalysis.Builder().build().apply {
        setAnalyzer(imageAnalyzerExecutor, mlKitAnalyzer)
    }

    private fun buildFaceDetectorOptions(): FaceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .build()

    interface Callback {
        fun onFacesDetected(faces: List<Face>)
    }
}