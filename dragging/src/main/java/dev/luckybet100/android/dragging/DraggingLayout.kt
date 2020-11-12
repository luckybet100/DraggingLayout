package dev.luckybet100.android.dragging

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import androidx.core.view.children
import dev.luckybet100.android.dragging.drag.DragDelegate
import dev.luckybet100.android.dragging.drag.DragDelegateImpl
import dev.luckybet100.android.dragging.utils.getMainPointerPosition
import kotlin.math.max
import kotlin.math.min

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

        override fun translate(index: Int, dx: Int, dy: Int) {
            getChildAt(index).left += dx
            getChildAt(index).right += dx
            getChildAt(index).top += dy
            getChildAt(index).bottom += dy
            invalidate()
        }
    }

    private val dragDelegate: DragDelegate =
        DragDelegateImpl(boundsProvider)

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private var scaleFactor = 1f

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            val index = dragDelegate.getDraggingIndex()
            if (index == -1) {
                return false
            }
            assert(index in 0..childCount)
            scaleFactor = getChildAt(index).scaleX
            return true
        }


        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val index = dragDelegate.getDraggingIndex()
            if (index == -1) {
                return false
            }
            assert(index in 0..childCount)
            scaleFactor *= detector.scaleFactor
            scaleFactor = max(0.1f, min(scaleFactor, 5.0f))
            getChildAt(index).scaleX = scaleFactor
            getChildAt(index).scaleY = scaleFactor
            invalidate()
            return true
        }

    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)


    init {
        setOnTouchListener { _, motionEvent ->
            scaleDetector.onTouchEvent(motionEvent)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    try {
                        val (actionX, actionY) = getMainPointerPosition(motionEvent)
                        return@setOnTouchListener dragDelegate.start(actionX, actionY)
                    } catch (exception: IllegalArgumentException) {
                        return@setOnTouchListener false
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    try {
                        val (actionX, actionY) = getMainPointerPosition(motionEvent)
                        return@setOnTouchListener dragDelegate.update(actionX, actionY)
                    } catch (exception: IllegalArgumentException) {
                        return@setOnTouchListener false
                    }
                }
                MotionEvent.ACTION_UP -> {
                    return@setOnTouchListener dragDelegate.end()
                }
            }
            false
        }
    }

}