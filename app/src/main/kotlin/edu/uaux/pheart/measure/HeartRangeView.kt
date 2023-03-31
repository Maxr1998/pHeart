package edu.uaux.pheart.measure

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import edu.uaux.pheart.R

class HeartRangeView : View {
    private var _lowerBound: Int = 0
    private var _upperBound: Int = 0
    private var _currentValue: Int = 0
    private var _goodStart: Int = 0
    private var _goodEnd: Int = 0
    private var _markerWidth: Float = 0f

    private lateinit var leftBoundsTextPaint: TextPaint
    private lateinit var centeredBoundsTextPaint: TextPaint
    private lateinit var rightBoundsTextPaint: TextPaint
    private lateinit var markerTextPaint: TextPaint

    private var warningPaint = Paint().apply {
        color = Color.YELLOW
    }

    private var goodPaint = Paint().apply {
        color = Color.GREEN
    }

    private val markerPaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = resources.displayMetrics.density * 5
    }

    var lowerBound get() = _lowerBound
        set(value) {
        _lowerBound = value
        invalidate()
    }
    var upperBound get() = _upperBound
        set(value) {
        _upperBound = value
        invalidate()
    }
    var currentValue get() = _currentValue
        set(value) {
        _currentValue = value
        invalidate()
    }
    var goodStart get() = _goodStart
        set(value) {
        _goodStart = value
        invalidate()
    }
    var goodEnd get() = _goodEnd
        set(value) {
        _goodEnd = value
        invalidate()
    }
    var markerWidth get() = _markerWidth
        set(value) {
        _markerWidth = value
        invalidate()
    }
    var warningPaintColor get() = warningPaint.color
        set(value) {
        warningPaint.color = value
        invalidate()
    }
    var goodPaintColor get() = goodPaint.color
        set(value) {
        goodPaint.color = value
        invalidate()
    }

    var markerPaintColor get() = markerPaint.color
        set(value) {
        markerPaint.color = value
        invalidate()
    }

    var markerTextWidth get() = markerTextPaint.measureText(currentValue.toString())
        set(value) {
        markerTextPaint.textSize = value
        invalidate()
    }

    var legendColor: Int = Color.BLACK
        set(value) {
        field = value
        leftBoundsTextPaint.color = value
        centeredBoundsTextPaint.color = value
        rightBoundsTextPaint.color = value
        invalidate()
    }


    /**
     * In the example view, this drawable is drawn above the text.
     */
    var exampleDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle,
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.HeartRangeView, defStyle, 0,
        )

        _lowerBound = a.getInteger(R.styleable.HeartRangeView_lowerBound, 0)
        _upperBound = a.getInteger(R.styleable.HeartRangeView_upperBound, 100)
        _goodStart = a.getInteger(R.styleable.HeartRangeView_goodStart, 60)
        _goodEnd = a.getInteger(R.styleable.HeartRangeView_goodEnd, 80)
        _currentValue = a.getInteger(R.styleable.HeartRangeView_currentValue, 70)

        warningPaint.color = a.getColor(R.styleable.HeartRangeView_warningColor, Color.YELLOW)
        goodPaint.color = a.getColor(R.styleable.HeartRangeView_goodColor, Color.GREEN)
        _markerWidth = a.getDimension(R.styleable.HeartRangeView_markerWidth, resources.displayMetrics.density * 5)
        markerPaint.color = a.getColor(R.styleable.HeartRangeView_markerColor, Color.GRAY)

        val legendColor = a.getColor(R.styleable.HeartRangeView_legendColor, Color.BLACK)
        val legendTextSize = a.getDimension(R.styleable.HeartRangeView_legendTextSize, resources.displayMetrics.density * 16)

        a.recycle()

        leftBoundsTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
            textSize = legendTextSize
            color = legendColor
        }

        centeredBoundsTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
            textSize = legendTextSize
            color = legendColor
        }

        rightBoundsTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.RIGHT
            textSize = legendTextSize
            color = legendColor
        }

        markerTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
            textSize = legendTextSize
            color = legendColor
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val valueRange = upperBound - lowerBound

        val greenStartPercent = (goodStart - lowerBound) / valueRange.toFloat()
        val greenEndPercent = (goodEnd - lowerBound) / valueRange.toFloat()

        val currentValuePercent = (currentValue - lowerBound) / valueRange.toFloat()

        canvas.drawRoundRect(
            paddingLeft.toFloat(),
            contentHeight.toFloat() * 0.3f,
            contentWidth.toFloat(),
            contentHeight.toFloat() * 0.7f,
            16f,
            16f,
            warningPaint,
        )

        canvas.drawRect(
            contentWidth.toFloat() * greenStartPercent,
            contentHeight.toFloat() * 0.3f,
            contentWidth.toFloat() * greenEndPercent,
            contentHeight.toFloat() * 0.7f,
            goodPaint,
        )

        // Same but with drawRoundRect and 16f radius
        canvas.drawRoundRect(
            contentWidth.toFloat() * currentValuePercent - markerWidth / 2,
            contentHeight.toFloat() * 0.2f,
            contentWidth.toFloat() * currentValuePercent + markerWidth / 2,
            contentHeight.toFloat() * 0.8f,
            32f,
            32f,
            markerPaint,
        )

        canvas.drawText(
            lowerBound.toString(),
            paddingLeft.toFloat(),
            contentHeight.toFloat() * 0.9f,
            leftBoundsTextPaint
        )

        canvas.drawText(
            goodStart.toString(),
            contentWidth.toFloat() * greenStartPercent,
            contentHeight.toFloat() * 0.9f,
            centeredBoundsTextPaint
        )

        canvas.drawText(
            goodEnd.toString(),
            contentWidth.toFloat() * greenEndPercent,
            contentHeight.toFloat() * 0.9f,
            centeredBoundsTextPaint
        )

        canvas.drawText(
            upperBound.toString(),
            contentWidth.toFloat(),
            contentHeight.toFloat() * 0.9f,
            rightBoundsTextPaint
        )

        canvas.drawText(
            currentValue.toString(),
            contentWidth.toFloat() * currentValuePercent,
            contentHeight.toFloat() * 0.15f,
            centeredBoundsTextPaint
        )
    }
}