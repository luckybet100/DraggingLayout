package dev.luckybet100.android.dragging.rotation

import android.graphics.Matrix
import android.util.Log
import android.view.MotionEvent
import dev.luckybet100.android.dragging.utils.getPointerPosition


class MatrixGestureDetector(private val listener: OnRotationGestureListener) {

    interface OnRotationGestureListener {

        fun onRotationBegin(detector: MatrixGestureDetector)
        fun onRotation(detector: MatrixGestureDetector)

    }

    private var fx = 0f
    private var fy = 0f
    private var sx = 0f
    private var sy = 0f
    private var pointerIndex1: Int = INVALID_POINTER_ID
    private var pointerIndex2: Int = INVALID_POINTER_ID

    private val originalMatrixInverted = Matrix()
    var originalMatrix = Matrix()
        set(value) {
            field = value
            value.invert(originalMatrixInverted)
        }
    var localMatrix = Matrix()
    val matrix = Matrix()

    private val originalPointsArray = FloatArray(4)
    private val newPointsArray = FloatArray(4)
    private val originalPoints = FloatArray(4)
    private val newPoints = FloatArray(4)

    fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> pointerIndex1 = event.getPointerId(event.actionIndex)
            MotionEvent.ACTION_POINTER_DOWN -> {
                pointerIndex2 = event.getPointerId(event.actionIndex)
                getPointerPosition(event, pointerIndex1).let {
                    fx = it.first
                    fy = it.second
                }
                getPointerPosition(event, pointerIndex2).let {
                    sx = it.first
                    sy = it.second
                }
                listener.onRotationBegin(this)
            }
            MotionEvent.ACTION_MOVE -> if (pointerIndex1 != INVALID_POINTER_ID && pointerIndex2 != INVALID_POINTER_ID) {
                val (nfx, nfy) = getPointerPosition(event, pointerIndex1)
                val (nsx, nsy) = getPointerPosition(event, pointerIndex2)
                originalPointsArray[0] = fx
                originalPointsArray[1] = fy
                originalPointsArray[2] = sx
                originalPointsArray[3] = sy
                newPointsArray[0] = nfx
                newPointsArray[1] = nfy
                newPointsArray[2] = nsx
                newPointsArray[3] = nsy
                originalMatrixInverted.mapPoints(originalPoints, originalPointsArray)
                originalMatrixInverted.mapPoints(newPoints, newPointsArray)
                matrix.setPolyToPoly(originalPoints, 0, newPoints, 0, 2)
                listener.onRotation(this)
            }
            MotionEvent.ACTION_UP -> pointerIndex1 = INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> pointerIndex2 = INVALID_POINTER_ID
            MotionEvent.ACTION_CANCEL -> {
                pointerIndex1 = INVALID_POINTER_ID
                pointerIndex2 = INVALID_POINTER_ID
            }
        }
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    init {
        pointerIndex1 = INVALID_POINTER_ID
        pointerIndex2 = INVALID_POINTER_ID
    }
}