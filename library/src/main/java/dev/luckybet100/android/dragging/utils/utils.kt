package dev.luckybet100.android.dragging.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import java.lang.Math.sqrt
import java.lang.Math.toDegrees
import kotlin.math.atan2
import kotlin.math.round
import kotlin.math.sign

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

fun dp(ctx: Context, value: Float): Float = dp(ctx.resources, value)
fun dp(resources: Resources, value: Float): Float = dp(resources.displayMetrics, value)
fun dp(dm: DisplayMetrics, value: Float): Float =
    round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm))

fun loadBitmap(resources: Resources, resId: Int): Bitmap {
    val drawable = resources.getDrawable(resId)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)
    return bitmap
}

fun distance2(a: PointF, b: PointF) = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)

fun View.setMatrix(matrix: Matrix) {
    val values = FloatArray(9)
    matrix.getValues(values)
    val translateX = values[Matrix.MTRANS_X]
    val translateY = values[Matrix.MTRANS_Y]
    val s1 = sign(values[Matrix.MSCALE_X]) // TODO not supported yet
    val s2 = sign(values[Matrix.MSCALE_Y]) // TODO not supported yet
    val scaleX = sqrt((values[Matrix.MSCALE_X] * values[Matrix.MSCALE_X] + values[Matrix.MSKEW_X] * values[Matrix.MSKEW_X]).toDouble())
    val scaleY = sqrt((values[Matrix.MSCALE_Y] * values[Matrix.MSCALE_Y] + values[Matrix.MSKEW_Y] * values[Matrix.MSKEW_Y]).toDouble())
    val rotation = atan2(values[Matrix.MSKEW_Y].toDouble(), values[Matrix.MSCALE_Y].toDouble())
    translationX = translateX
    translationY = translateY
    this.rotation = toDegrees(rotation).toFloat()
    this.scaleX = scaleX.toFloat()
    this.scaleY = scaleY.toFloat()
}

fun View.globalMatrix(matrix: Matrix) {
    val parent: ViewParent = parent
    if (parent is View) {
        val vp = parent as View
        vp.globalMatrix(matrix)
        matrix.preTranslate(-vp.scrollX.toFloat(), -vp.scrollY.toFloat())
    }
    matrix.preTranslate(left.toFloat(), top.toFloat())
    matrix.preConcat(getMatrix())
}