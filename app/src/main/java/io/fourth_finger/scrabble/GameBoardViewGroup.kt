package io.fourth_finger.scrabble

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.view.WindowManager
import kotlin.math.max
import kotlin.math.min

class GameBoardViewGroup(
    context: Context,
    val gridSize: Int = 20,
    val squareSize: Int = 192
) : ViewGroup(context) {

    private var scaleFactor = 1.0f
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialTranslationX = 0f
    private var initialTranslationY = 0f
    private var currentTranslationX = 0f
    private var currentTranslationY = 0f


    private val scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor

            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            scaleFactor = if(isLandscape){
                max(
                    scaleFactor,
                    windowManager.defaultDisplay.height / (squareSize * gridSize).toFloat()
                )
            } else {
                max(
                    scaleFactor,
                    windowManager.defaultDisplay.width / (squareSize * gridSize).toFloat()
                )
            }
            scaleFactor = min(scaleFactor, 1.5f)

            val contentWidth = squareSize * gridSize * scaleFactor
            val contentHeight = squareSize * gridSize * scaleFactor
            currentTranslationX = currentTranslationX.coerceIn(0f, contentWidth)
            currentTranslationY = currentTranslationY.coerceIn(0f, contentHeight)

            invalidate()
            return true
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val index = row * gridSize + col
                if (index < childCount) {
                    val child = getChildAt(index)

                    val childLeft = col * squareSize
                    val childTop = row * squareSize

                    child.layout(
                        childLeft,
                        childTop,
                        childLeft + squareSize,
                        childTop + squareSize
                    )

                    val color = getGradientColor(index, childCount)
                    child.setBackgroundColor(color)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if(event.pointerCount == 1) {
                    initialTouchX = event.x
                    initialTouchY = event.y

                    initialTranslationX = currentTranslationX
                    initialTranslationY = currentTranslationY
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    scaleGestureDetector.onTouchEvent(event)
                }
                val dx = event.x - initialTouchX
                val dy = event.y - initialTouchY

                val newTranslationX = initialTranslationX + dx
                val newTranslationY = initialTranslationY + dy

                val contentWidth = squareSize * gridSize * scaleFactor
                val contentHeight = squareSize * gridSize * scaleFactor


                currentTranslationX = when {
                    contentWidth <= width -> 0f
                    newTranslationX > 0 -> 0f
                    newTranslationX < (width - contentWidth) -> (width - contentWidth).toFloat()
                    else -> newTranslationX
                }

                currentTranslationY = when {
                    contentHeight <= height -> 0f
                    newTranslationY > 0 -> 0f
                    newTranslationY < (height - contentHeight) -> (height - contentHeight).toFloat()
                    else -> newTranslationY
                }
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var maxWidth = 0
        var maxHeight = 0

        maxWidth = maxOf(maxWidth, squareSize)
        maxHeight = maxOf(maxHeight, squareSize)

        val desiredWidth = if (widthMode == MeasureSpec.EXACTLY) widthSize else maxWidth
        val desiredHeight = if (heightMode == MeasureSpec.EXACTLY) heightSize else maxHeight

        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawColor(Color.BLACK)
        canvas.save()

        canvas.scale(scaleFactor, scaleFactor)
        canvas.translate(currentTranslationX / scaleFactor, currentTranslationY / scaleFactor)

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            drawChild(canvas, child, drawingTime)
        }

        canvas.restore()
    }


    private fun getGradientColor(index: Int, totalCells: Int): Int {
        val ratio = if (totalCells > 1) index.toFloat() / (totalCells - 1) else 0.0f
        val r = (255 * ratio).toInt()
        val g = (255 * (1 - ratio)).toInt()
        val b = 0
        return Color.rgb(r, g, b)
    }
}
