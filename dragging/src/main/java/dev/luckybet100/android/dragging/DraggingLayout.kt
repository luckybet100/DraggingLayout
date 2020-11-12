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
import dev.luckybet100.android.dragging.rotation.RotationGestureDetector
import dev.luckybet100.android.dragging.utils.getPointerPosition
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
            scaleFactor = max(0.2f, min(scaleFactor, 10.0f))
            getChildAt(index).scaleX = scaleFactor
            getChildAt(index).scaleY = scaleFactor
            invalidate()
            return true
        }

    }

    private val rotationListener = object : RotationGestureDetector.OnRotationGestureListener {

        private var angle = 0f

        override fun onRotationBegin(rotationDetector: RotationGestureDetector) {
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
            getChildAt(index).rotation = angle + detector.angle
            invalidate()
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)
    private val rotationDetector = RotationGestureDetector(rotationListener)

    init {
        setOnTouchListener { _, motionEvent ->
            scaleDetector.onTouchEvent(motionEvent)
            rotationDetector.onTouchEvent(motionEvent)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    try {
                        val (actionX, actionY) = getPointerPosition(motionEvent, 0)
                        return@setOnTouchListener dragDelegate.start(
                            actionX.toInt(),
                            actionY.toInt()
                        )
                    } catch (exception: IllegalArgumentException) {
                        return@setOnTouchListener false
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    try {
                        val (actionX, actionY) = getPointerPosition(motionEvent, 0)
                        return@setOnTouchListener dragDelegate.update(
                            actionX.toInt(),
                            actionY.toInt()
                        )
                    } catch (exception: IllegalArgumentException) {
                        return@setOnTouchListener false
                    }
                }
                MotionEvent.ACTION_UP -> {
                    return@setOnTouchListener dragDelegate.end()
                }
                MotionEvent.ACTION_CANCEL -> {
                    return@setOnTouchListener dragDelegate.end()
                }
            }
            false
        }
    }

}