package dev.luckybet100.android.dragging

import android.graphics.Matrix
import android.graphics.Rect

interface DragChildrenBoundsProvider {

    data class ChildDescription(val rect: Rect)

    fun children(): Sequence<ChildDescription>
    fun translate(index: Int, dx: Int, dy: Int)
    fun scale(index: Int, scale: Float, pivotX: Float, pivotY: Float)
    fun rotate(index: Int, rotation: Float, pivotX: Float, pivotY: Float)
    fun setMatrix(index: Int, matrix: Matrix)
}