package dev.luckybet100.android.dragging.utils

import android.view.MotionEvent

fun getMainPointerPosition(motionEvent: MotionEvent): Pair<Int, Int> {
    val mainPointerIndex = motionEvent.getPointerId(0)
    val x = motionEvent.getX(mainPointerIndex).toInt()
    val y = motionEvent.getY(mainPointerIndex).toInt()
    return x to y
}