package edu.uaux.pheart.measure

import android.graphics.PointF

interface MeasurementCallback {
    /**
     * Called when a new luminance measurement from the camera was performed by the analyzer.
     */
    fun onMeasurementTaken(timestamp: Long, averageLuminance: Double)

    /**
     * Called when the measurement was cancelled by the analyzer, e.g., because no face was detected anymore.
     */
    fun onMeasurementCancelled()

    /**
     * Used for debugging.
     */
    fun onShowPoints(points: List<PointF>, imageWidth: Int, imageHeight: Int) = Unit
}