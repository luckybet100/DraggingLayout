package dev.luckybet100.android.dragging

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import dev.luckybet100.android.dragging.drag.DragDelegate
import dev.luckybet100.android.dragging.drag.DragDelegateImpl
import dev.luckybet100.android.dragging.rotation.RotationGestureDetector
import dev.luckybet100.android.dragging.utils.getPointerPosition
import kotlin.math.max
import kotlin.math.min

open class DraggingLayout : FrameLayout, View.OnTouchListener {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun notifyItemsChanged() {
        dragDelegate.end()
    }

    val draggingElementIndex: Int
        get() = dragDelegate.getDraggingIndex()

    fun children(): Sequence<DragChildrenBoundsProvider.ChildDescription> = children.map {
        val rect = Rect()
        it.getHitRect(rect)
        DragChildrenBoundsProvider.ChildDescription(rect)
    }

    private val boundsProvider: DragChildrenBoundsProvider = object : DragChildrenBoundsProvider {

        override fun children(): Sequence<DragChildrenBoundsProvider.ChildDescription> =
            this@DraggingLayout.children()

        override fun translate(index: Int, dx: Int, dy: Int) {
            assert(index in 0..childCount)
            getChildAt(index).x += dx
            getChildAt(index).y += dy
            invalidate()
        }

        override fun scale(index: Int, scale: Float, pivotX: Float, pivotY: Float) {
            assert(index in 0..childCount)
            with(getChildAt(index)) {
                scaleX = scale
                scaleY = scale
            }
            invalidate()
        }

        override fun rotate(index: Int, rotation: Float, pivotX: Float, pivotY: Float) {
            assert(index in 0..childCount)
            getChildAt(index).rotation = rotation
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
            scaleFactor = max(0.2f, min(scaleFactor, 10.0f))
            boundsProvider.scale(index, scaleFactor, detector.focusX, detector.focusY)
            invalidate()
            return true
        }

    }

    private val rotationListener = object : RotationGestureDetector.OnRotationGestureListener {

        private var angle = 0f

        override fun onRotationBegin(detector: RotationGestureDetector) {
            val index = dragDelegate.getDraggingIndex()
            if (index == -1) {
                return
            }
            assert(index in 0..childCount)
            angle = getChildAt(index).rotation
        }

        override fun onRotation(detector: RotationGestureDetector) {
            val index = dragDelegate.getDraggingIndex()
            if (index == -1) {
                return
            }
            assert(index in 0..childCount)
            boundsProvider.rotate(index, angle + detector.angle, detector.focusX, detector.focusY)
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)
    private val rotationDetector = RotationGestureDetector(rotationListener)

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(motionEvent)
        rotationDetector.onTouchEvent(motionEvent)
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                return try {
                    val (actionX, actionY) = getPointerPosition(motionEvent, 0)
                    dragDelegate.start(
                        actionX.toInt(),
                        actionY.toInt()
                    )
                } catch (exception: IllegalArgumentException) {
                    false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                return try {
                    val (actionX, actionY) = getPointerPosition(motionEvent, 0)
                    dragDelegate.update(
                        actionX.toInt(),
                        actionY.toInt()
                    )
                } catch (exception: IllegalArgumentException) {
                    false
                }
            }
            MotionEvent.ACTION_UP -> {
                return dragDelegate.end()
            }
            MotionEvent.ACTION_CANCEL -> {
                return dragDelegate.end()
            }
        }
        return false
    }

    init { setOnTouchListener(this) }

}