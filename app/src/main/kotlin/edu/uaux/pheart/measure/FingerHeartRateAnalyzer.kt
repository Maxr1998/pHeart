package edu.uaux.pheart.measure

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class FingerHeartRateAnalyzer(
    private val callbackExecutor: Executor,
    private val callback: MeasurementCallback,
) : ImageAnalysis.Analyzer {

    private val backgroundExecutor: Executor = Executors.newSingleThreadExecutor()

    val useCase: ImageAnalysis = ImageAnalysis.Builder().build().also { analysis ->
        analysis.setAnalyzer(backgroundExecutor, this)
    }

    override fun analyze(image: ImageProxy) {
        require(image.format == ImageFormat.YUV_420_888)
        val timestamp = image.imageInfo.timestamp
        val yPlane = image.planes.first()
        val buffer = yPlane.buffer
        var sum = 0UL
        while (buffer.hasRemaining()) {
            sum += buffer.get().toUByte().toULong()
        }
        val average = sum.toDouble() / buffer.limit()

        callbackExecutor.execute {
            callback.onMeasurementTaken(timestamp, average)
        }

        image.close()
    }
}