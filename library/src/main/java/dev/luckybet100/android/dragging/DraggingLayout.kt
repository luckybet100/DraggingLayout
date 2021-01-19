package dev.luckybet100.android.dragging

import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import dev.luckybet100.android.dragging.drag.DragDelegate
import dev.luckybet100.android.dragging.drag.DragDelegateImpl
import dev.luckybet100.android.dragging.rotation.MatrixGestureDetector
import dev.luckybet100.android.dragging.utils.getPointerPosition
import dev.luckybet100.android.dragging.utils.setMatrix
import kotlin.math.max
import kotlin.math.min

open class DraggingLayout : FrameLayout, View.OnTouchListener {

    interface OnDragStartedListener {
        fun onDragStarted(started: Boolean)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var onDragStartedListener: OnDragStartedListener? = null

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

        override fun setMatrix(index: Int, matrix: Matrix) {
            assert(index in 0..childCount)
            getChildAt(index).setMatrix(matrix)
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

    private val matrixListener = object : MatrixGestureDetector.OnRotationGestureListener {

        private val tempMatrix = Matrix()

        override fun onRotationBegin(detector: MatrixGestureDetector) {
            val index = dragDelegate.getDraggingIndex()
            if (index == -1) {
                return
            }
            assert(index in 0..childCount)
            val matrix = Matrix()
            with(getChildAt(index)) {
                tempMatrix.reset()
                tempMatrix.preTranslate(-scrollX.toFloat(), -scrollY.toFloat())
                tempMatrix.preTranslate(left.toFloat(), top.toFloat())
                matrix.set(this.matrix)
                tempMatrix.preConcat(matrix)
            }
            detector.originalMatrix = Matrix(tempMatrix)
            detector.localMatrix = matrix
        }

        override fun onRotation(detector: MatrixGestureDetector) {
            val index = dragDelegate.getDraggingIndex()
            if (index == -1) {
                return
            }
            assert(index in 0..childCount)
            tempMatrix.reset()
            tempMatrix.setConcat(detector.localMatrix, detector.matrix)
            boundsProvider.setMatrix(index, tempMatrix)
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)
    private val matrixDetector = MatrixGestureDetector(matrixListener)

    private var dragging = false

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(motionEvent)
        matrixDetector.onTouchEvent(motionEvent)
        if (!dragging && dragDelegate.getDraggingIndex() != -1) {
            dragging = true
            onDragStartedListener?.onDragStarted(true)
        }
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
                if (dragging) {
                    dragging = false
                    onDragStartedListener?.onDragStarted(false)
                }
                return dragDelegate.end()
            }
            MotionEvent.ACTION_CANCEL -> {
                if (dragging) {
                    dragging = false
                    onDragStartedListener?.onDragStarted(false)
                }
                return dragDelegate.end()
            }
        }
        return false
    }

    init { setOnTouchListener(this) }

}