package dev.luckybet100.android.dragging

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.children
import dev.luckybet100.android.dragging.drag.DragDelegate
import dev.luckybet100.android.dragging.drag.DragDelegateImpl
import dev.luckybet100.android.dragging.utils.getMainPointerPosition

class DraggingLayout : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val boundsProvider: DragChildrenBoundsProvider = object : DragChildrenBoundsProvider {

        override fun children(): Sequence<DragChildrenBoundsProvider.ChildDescription> =
            children.map {
                val rect = Rect()
                it.getHitRect(rect)
                DragChildrenBoundsProvider.ChildDescription(rect)
            }

        override fun updateChild(
            index: Int,
            description: DragChildrenBoundsProvider.ChildDescription
        ) {
            assert(index in 0..childCount)
            description.rect.apply {
                getChildAt(index).left = left
                getChildAt(index).right = right
                getChildAt(index).top = top
                getChildAt(index).bottom = bottom
            }
            invalidate()
        }
    }

    private val dragDelegate: DragDelegate =
        DragDelegateImpl(boundsProvider)

    init {
        setOnTouchListener { _, motionEvent ->
            val (mainX, mainY) = getMainPointerPosition(motionEvent)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    return@setOnTouchListener dragDelegate.start(mainX, mainY)
                }
                MotionEvent.ACTION_MOVE -> {
                    return@setOnTouchListener dragDelegate.update(mainX, mainY)
                }
                MotionEvent.ACTION_UP -> {
                    dragDelegate.end()
                }
            }
            false
        }
    }

}