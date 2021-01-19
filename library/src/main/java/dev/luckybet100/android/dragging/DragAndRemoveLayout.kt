package dev.luckybet100.android.dragging

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import dev.luckybet100.android.dragging.utils.distance2
import dev.luckybet100.android.dragging.utils.dp
import dev.luckybet100.android.dragging.utils.loadBitmap

class DragAndRemoveLayout : DraggingLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var trashScale = 1f
    private val trashRadius = dp(context, 25f)
    private val trashIconHeight = dp(context, 8f)
    private val trashIconWidth = dp(context, 6.4f)
    private val trashStrokeWidth = dp(context, 2f)
    private val cross = loadBitmap(resources, R.drawable.ic_trash)
    private val crossRect = Rect(0, 0, cross.width, cross.height)

    private var trashSelected = false
    private var trashVisible = false
    private var trashAnimation: ValueAnimator? = null
    private var showAnimation: ValueAnimator? = null

    private val trashStrokePaint = Paint().apply {
        color = Color.WHITE
        alpha = 0
        style = Paint.Style.STROKE
        strokeWidth = trashStrokeWidth
    }

    private val trashFillPaint = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.FILL
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.drawCircle(
            width / 2f,
            height - trashRadius * 2f,
            trashRadius * trashScale,
            trashFillPaint
        )
        canvas.drawCircle(
            width / 2f,
            height - trashRadius * 2f,
            trashRadius * trashScale,
            trashStrokePaint
        )
        canvas.drawBitmap(
            cross,
            crossRect,
            RectF(
                (width / 2f - trashIconWidth * trashScale),
                (height - trashRadius * 2f - trashIconHeight * trashScale),
                (width / 2f + trashIconWidth * trashScale),
                (height - trashRadius * 2f + trashIconHeight * trashScale)
            ),
            trashStrokePaint
        )
        super.dispatchDraw(canvas)
    }


    private fun showSelectedArea() {
        if (trashSelected) return
        trashSelected = true
        trashAnimation?.cancel()
        trashAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                trashScale = 1f + it.animatedFraction / 3f
                trashFillPaint.color =
                    Color.argb(
                        (0x77 * it.animatedFraction).toInt(),
                        (0xFF * it.animatedFraction).toInt(),
                        0,
                        0
                    )
                if (it.animatedFraction == 1f)
                    trashAnimation = null
                invalidate()
            }
            duration = 150
            start()
        }
    }

    private fun hideSelectedArea() {
        if (!trashSelected) return
        trashSelected = false
        val alpha = trashFillPaint.color.shr(24).and(255)
        val red = trashFillPaint.color.shr(16).and(255)
        val scale = trashScale - 1f
        trashAnimation?.cancel()
        trashAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                trashScale = 1f + scale * (1f - it.animatedFraction)
                trashFillPaint.color =
                    Color.argb(
                        (alpha * (1f - it.animatedFraction)).toInt(),
                        (red * (1f - it.animatedFraction)).toInt(),
                        0,
                        0
                    )
                if (it.animatedFraction == 1f)
                    trashAnimation = null
                invalidate()
            }
            duration = 150
            start()
        }
    }

    private fun showTrash() {
        if (trashVisible) return
        trashVisible = true
        showAnimation?.cancel()
        showAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                trashStrokePaint.alpha = (it.animatedFraction * 255).toInt()
                invalidate()
            }
            duration = 300
            start()
        }
    }

    private fun hideTrash() {
        if (!trashVisible) return
        trashVisible = false
        showAnimation?.cancel()
        showAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                trashStrokePaint.alpha = ((1f - it.animatedFraction) * 255).toInt()
                invalidate()
            }
            duration = 300
            start()
        }
    }


    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val dragIndex = super.draggingElementIndex
        val result = super.onTouch(view, motionEvent)
        if (dragIndex != -1) {
            showTrash()
            val (x, y) = motionEvent.x to motionEvent.y
            val trashPosition = PointF(width / 2f, height - trashRadius * 2f)
            when (motionEvent.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (distance2(PointF(x, y), trashPosition) <= trashRadius * trashRadius) {
                        showSelectedArea()
                    } else {
                        hideSelectedArea()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (distance2(PointF(x, y), trashPosition) <= trashRadius * trashRadius) {
                        assert(dragIndex in 0..childCount)
                        getChildAt(dragIndex).apply {
                            pivotX = width / 2f
                            pivotY = height / 2f
                            animate().alpha(0f).withEndAction {
                                removeViewAt(dragIndex)
                                notifyItemsChanged()
                            }.scaleX(0f).scaleY(0f).apply {
                                duration = 300
                            }.start()
                            hideSelectedArea()
                        }
                    }
                    hideTrash()
                }
                MotionEvent.ACTION_CANCEL -> {
                    hideSelectedArea()
                    hideTrash()
                }
            }
        }
        return result
    }

}