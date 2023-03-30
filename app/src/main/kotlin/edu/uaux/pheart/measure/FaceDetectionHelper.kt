package edu.uaux.pheart.measure

import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.core.util.Consumer
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executor

object FaceDetectionHelper {
    private const val MIN_FACE_SIZE = 0.3f

    fun buildFaceDetector(): FaceDetector {
        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(MIN_FACE_SIZE)
            .build()
        return FaceDetection.getClient(faceDetectorOptions)
    }

    fun buildMlKitAnalyzer(
        faceDetector: FaceDetector,
        callbackExecutor: Executor,
        consumer: Consumer<MlKitAnalyzer.Result>,
    ): MlKitAnalyzer = MlKitAnalyzer(
        listOf(faceDetector),
        ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL,
        callbackExecutor,
        consumer,
    )
}