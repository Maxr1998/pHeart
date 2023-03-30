package edu.uaux.pheart.measure

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = resources.displayMetrics.density * 3
    }

    var points: List<PointF> = emptyList()
        set(value) {
            field = value
            invalidate()
        }
    var imageHeight: Int = 0
    var imageWidth: Int = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPoint(imageWidth.toFloat(), imageHeight.toFloat(), paint)
        points.forEach { point ->
            canvas.drawPoint(width - point.x * width / imageWidth, point.y * height / imageHeight, paint)
        }
    }
}