package dev.luckybet100.android.dragging.utils

import android.view.MotionEvent

fun getMainPointerPosition(motionEvent: MotionEvent): Pair<Int, Int> {
    val pointerIndex = motionEvent.findPointerIndex(0)
    val x = motionEvent.getX(pointerIndex).toInt()
    val y = motionEvent.getY(pointerIndex).toInt()
    return x to y
}