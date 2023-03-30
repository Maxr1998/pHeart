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
    private var _greenStart: Int = 0
    private var _greenEnd: Int = 0
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

    val lowerBound get() = _lowerBound
    val upperBound get() = _upperBound
    val currentValue get() = _currentValue
    val greenStart get() = _greenStart
    val greenEnd get() = _greenEnd
    val markerWidth get() = _markerWidth


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
        _greenStart = a.getInteger(R.styleable.HeartRangeView_greenStart, 60)
        _greenEnd = a.getInteger(R.styleable.HeartRangeView_greenEnd, 80)
        _currentValue = a.getInteger(R.styleable.HeartRangeView_currentValue, 70)

        warningPaint.color = a.getColor(R.styleable.HeartRangeView_warningColor, Color.YELLOW)
        goodPaint.color = a.getColor(R.styleable.HeartRangeView_goodColor, Color.GREEN)
        _markerWidth = a.getDimension(R.styleable.HeartRangeView_markerWidth, resources.displayMetrics.density * 5)

        a.recycle()

        // Set up a default TextPaint object
        leftBoundsTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
            textSize = 40f
        }

        centeredBoundsTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
            textSize = 40f
        }

        rightBoundsTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.RIGHT
            textSize = 40f
        }

        markerTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
            textSize = 40f
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

        val greenStartPercent = (greenStart - lowerBound) / valueRange.toFloat()
        val greenEndPercent = (greenEnd - lowerBound) / valueRange.toFloat()

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
            16f,
            16f,
            markerPaint,
        )

        canvas.drawText(
            lowerBound.toString(),
            paddingLeft.toFloat(),
            contentHeight.toFloat() * 0.80f,
            leftBoundsTextPaint
        )

        canvas.drawText(
            greenStart.toString(),
            contentWidth.toFloat() * greenStartPercent,
            contentHeight.toFloat() * 0.825f,
            centeredBoundsTextPaint
        )

        canvas.drawText(
            greenEnd.toString(),
            contentWidth.toFloat() * greenEndPercent,
            contentHeight.toFloat() * 0.825f,
            centeredBoundsTextPaint
        )

        canvas.drawText(
            upperBound.toString(),
            contentWidth.toFloat(),
            contentHeight.toFloat() * 0.825f,
            rightBoundsTextPaint
        )

        canvas.drawText(
            currentValue.toString(),
            contentWidth.toFloat() * currentValuePercent,
            contentHeight.toFloat() * 1f,
            centeredBoundsTextPaint
        )





    }
}