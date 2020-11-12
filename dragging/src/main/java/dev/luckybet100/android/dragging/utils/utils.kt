package dev.luckybet100.android.dragging.utils

import android.view.MotionEvent
import kotlin.math.atan2

fun getPointerPosition(motionEvent: MotionEvent, index: Int): Pair<Float, Float> {
    val pointerIndex = motionEvent.findPointerIndex(index)
    val x = motionEvent.getX(pointerIndex)
    val y = motionEvent.getY(pointerIndex)
    return x to y
}

fun angleBetweenLines(
    fx: Float,
    fy: Float,
    sx: Float,
    sy: Float,
    nfx: Float,
    nfy: Float,
    nsx: Float,
    nsy: Float
): Float {
    val angle1 = atan2((fy - sy).toDouble(), (fx - sx).toDouble()).toFloat()
    val angle2 = atan2((nfy - nsy).toDouble(), (nfx - nsx).toDouble()).toFloat()
    var angle = Math.toDegrees(angle2 - angle1.toDouble()).toFloat() % 360
    if (angle < -180f) angle += 360.0f
    if (angle > 180f) angle -= 360.0f
    return angle
}