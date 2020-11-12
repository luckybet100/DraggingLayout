package dev.luckybet100.android.dragging.rotation

import android.view.MotionEvent
import dev.luckybet100.android.dragging.utils.angleBetweenLines
import dev.luckybet100.android.dragging.utils.getPointerPosition


class RotationGestureDetector(private val listener: OnRotationGestureListener) {

    interface OnRotationGestureListener {

        fun onRotationBegin(detector: RotationGestureDetector)
        fun onRotation(detector: RotationGestureDetector)

    }

    private var fx = 0f
    private var fy = 0f
    private var sx = 0f
    private var sy = 0f
    private var pointerIndex1: Int = INVALID_POINTER_ID
    private var pointerIndex2: Int = INVALID_POINTER_ID
    var angle = 0f
        private set

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
                angle = angleBetweenLines(fx, fy, sx, sy, nfx, nfy, nsx, nsy)
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