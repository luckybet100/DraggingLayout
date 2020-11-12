package dev.luckybet100.android.dragging.drag

import dev.luckybet100.android.dragging.DragChildrenBoundsProvider

class DragDelegateImpl(
    override val childrenDelegate: DragChildrenBoundsProvider
) : DragDelegate {

    private var state: DragDelegate.State = DragDelegate.State.Idle

    override fun start(x: Int, y: Int): Boolean {
        assert(state == DragDelegate.State.Idle)
        for ((index, child) in childrenDelegate.children().withIndex()) {
            if (child.rect.contains(x, y)) {
                state = DragDelegate.State.Dragging(x, y, index)
                return true
            }
        }
        return false
    }

    override fun update(x: Int, y: Int): Boolean {
        val state = state as? DragDelegate.State.Dragging ?: return false
        val (dx, dy) = (x - state.startX) to (y - state.startY)
        childrenDelegate.translate(state.index, dx, dy)
        this.state = DragDelegate.State.Dragging(x, y, state.index)
        return true
    }

    override fun end(): Boolean {
        val result = state != DragDelegate.State.Idle
        state = DragDelegate.State.Idle
        return result
    }

    override fun getDraggingIndex(): Int = (state as? DragDelegate.State.Dragging)?.index ?: -1
}