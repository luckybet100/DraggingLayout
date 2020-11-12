package dev.luckybet100.android.dragging

import android.graphics.Rect

interface DragChildrenBoundsProvider {

    data class ChildDescription(val rect: Rect)

    fun children(): Sequence<ChildDescription>
    fun translate(index: Int, dx: Int, dy: Int)

}