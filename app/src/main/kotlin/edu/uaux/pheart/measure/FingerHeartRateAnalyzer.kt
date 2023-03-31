package edu.uaux.pheart.measure

import android.graphics.ImageFormat
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class FingerHeartRateAnalyzer(
    private val callbackExecutor: Executor,
    private val callback: MeasureCallback,
) : ImageAnalysis.Analyzer {

    private val backgroundExecutor: Executor = Executors.newSingleThreadExecutor()

    val useCase: ImageAnalysis = ImageAnalysis.Builder().apply {
        setTargetResolution(Size(480, 640))
    }.build().also { analysis ->
        analysis.setAnalyzer(backgroundExecutor, this)
    }

    override fun analyze(image: ImageProxy) {
        require(image.format == ImageFormat.YUV_420_888)
        val timestamp = image.imageInfo.timestamp
        val yPlane = image.planes[0]
        val buffer = yPlane.buffer
        var sum = 0UL
        while (buffer.hasRemaining()) {
            sum += buffer.get().toUByte().toULong()
        }
        val average = sum.toDouble() / buffer.limit()
        val measurement = LuminanceMeasurement(timestamp, average)

        callbackExecutor.execute {
            callback.onLuminanceMeasured(measurement)
        }

        image.close()
    }
}