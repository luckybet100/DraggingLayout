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
                state = DragDelegate.State.Dragging(x, y, index, child)
                return true
            }
        }
        return false
    }

    override fun update(x: Int, y: Int): Boolean {
        val state = state as? DragDelegate.State.Dragging ?: return false
        val (dx, dy) = (x - state.startX) to (y - state.startY)
        state.child.rect.left += dx
        state.child.rect.right += dx
        state.child.rect.top += dy
        state.child.rect.bottom += dy
        childrenDelegate.updateChild(state.index, state.child)
        this.state = DragDelegate.State.Dragging(x, y, state.index, state.child)
        return true
    }

    override fun end() { state = DragDelegate.State.Idle }
}