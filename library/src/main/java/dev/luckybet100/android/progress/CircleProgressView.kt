package dev.luckybet100.android.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import dev.luckybet100.android.dragging.R
import dev.luckybet100.android.dragging.utils.dp


open class CircleProgressView : View {

    var progress = 1f
        set(value) {
            field = value
            invalidate()
        }

    var color: Int = Color.WHITE
        set(value) {
            paint.color = value
            field = value
        }

    var strokeWidth: Float = 0f
        set(value) {
            paint.strokeWidth = value
            field = value
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        parseStyle(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        parseStyle(context, attrs)
    }

    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = dp(context, 5f)
    }

    private fun parseStyle(context: Context, attrs: AttributeSet?) {
        if (attrs == null) return
        val style =
            context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView, 0, 0)
        try {
            with(style) {
                color = getColor(R.styleable.CircleProgressView_color, 0)
                strokeWidth = getDimension(R.styleable.CircleProgressView_strokeWidth, 0f)
                progress = getFloat(R.styleable.CircleProgressView_progress, 0f)
            }
        } finally {
            style.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(
            paint.strokeWidth / 2f,
            paint.strokeWidth / 2f,
            width - paint.strokeWidth / 2f,
            height - paint.strokeWidth / 2f,
            -90f,
            -360 * progress,
            false,
            paint
        )
    }

}