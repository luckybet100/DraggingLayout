package dev.luckybet100.android.progress

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

class CircleClockProgressView : CircleProgressView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var time: Int = 0
        private set

    fun setTime(seconds: Int, minutes: Int) {
       time = seconds + minutes * 60
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

    }

}