package io.fourth_finger.scrabble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.view.DragEvent
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.children
import kotlin.math.floor
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

    private val scaleGestureDetector = ScaleGestureDetector(
        context,
        ScaleListener()
    )

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor

            // Limit scale to the smaller of the width or height
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val windowHeight = windowManager.defaultDisplay.height
            val windowWidth = windowManager.defaultDisplay.width
            scaleFactor = if (windowHeight < windowWidth) {
                max(
                    scaleFactor,
                    windowHeight / (squareSize * gridSize).toFloat()
                )
            } else {
                max(
                    scaleFactor,
                    windowWidth / (squareSize * gridSize).toFloat()
                )
            }
            scaleFactor = min(scaleFactor, 1.5f)

            // Update the translation
            currentTranslationX *= scaleFactor
            currentTranslationY *= scaleFactor

            updateChildren()
            return true
        }

    }

    init {
        setOnDragListener { _, event ->
            val view = event.localState
            if (view is TileView) {
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        return@setOnDragListener true
                    }

                    DragEvent.ACTION_DROP -> {

                        // Calculate row and column based on drawing size
                        val adjustedDropX = (event.x - currentTranslationX) / scaleFactor
                        val adjustedDropY = (event.y - currentTranslationY) / scaleFactor
                        val targetCellX = floor(adjustedDropX / squareSize)
                        val targetCellY = floor(adjustedDropY / squareSize)

                        // Margins are based on layout size
                        val targetX = targetCellX * squareSize
                        val targetY = targetCellY * squareSize
                        val parent = view.parent as ViewGroup
                        parent.removeView(view)
                        addView(view)
                        view.layoutParams = MarginLayoutParams(gridSize, gridSize)
                        (view.layoutParams as MarginLayoutParams).leftMargin = targetX.toInt()
                        (view.layoutParams as MarginLayoutParams).topMargin = targetY.toInt()
                        requestLayout()
                        view.visibility = VISIBLE
                        return@setOnDragListener true
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        return@setOnDragListener true
                    }

                    else -> return@setOnDragListener false
                }
            }
            false
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        // The grid itself
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val index = row * gridSize + col
                val child = getChildAt(index)

                val childLeft = (col * squareSize)
                val childTop = (row * squareSize)

                child.layout(
                    childLeft.toInt(),
                    childTop.toInt(),
                    childLeft.toInt() + squareSize,
                    childTop.toInt() + squareSize
                )
            }
        }

        // Tile added to the grid
        for (i in gridSize * gridSize until childCount) {
            val child = getChildAt(i)
            val childLeft = (child.layoutParams as MarginLayoutParams).leftMargin
            val childTop = (child.layoutParams as MarginLayoutParams).topMargin
            val childRight = childLeft + squareSize
            val childBottom = childTop + squareSize
            child.layout(childLeft, childTop, childRight, childBottom)
        }
        updateChildren()
    }

    private fun updateChildren() {
        val gapSize = ((squareSize - (squareSize * scaleFactor)) / 2)

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val index = row * gridSize + col
                val child = getChildAt(index)
                child.translationX = currentTranslationX - ((2 * col * gapSize) + gapSize)
                child.translationY = currentTranslationY - ((2 * row * gapSize) + gapSize)
                child.scaleX = scaleFactor
                child.scaleY = scaleFactor
            }
        }

        for (i in gridSize * gridSize until childCount) {
            val child = getChildAt(i)
            val childLeft = (child.layoutParams as MarginLayoutParams).leftMargin
            val childTop = (child.layoutParams as MarginLayoutParams).topMargin
            val childCol = (childLeft / squareSize)
            val childRow = (childTop / squareSize)
            child.translationX = currentTranslationX - ((2 * childCol * gapSize) + gapSize)
            child.translationY = currentTranslationY - ((2 * childRow * gapSize) + gapSize)
            child.scaleX = scaleFactor
            child.scaleY = scaleFactor
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.pointerCount == 1) {
                    initialTouchX = x
                    initialTouchY = y
                    initialTranslationX = currentTranslationX
                    initialTranslationY = currentTranslationY
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    scaleGestureDetector.onTouchEvent(event)
                } else {

                    // Single point drag
                    val contentWidth = squareSize * gridSize * scaleFactor
                    val contentHeight = squareSize * gridSize * scaleFactor

                    val dx = x - initialTouchX
                    val dy = y - initialTouchY

                    val newTranslationX = initialTranslationX + dx
                    val newTranslationY = initialTranslationY + dy

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
                    updateChildren()
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    companion object {

        class DragOnDownTouchListener() : OnTouchListener {
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                if (view is TileView) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            val dragShadowBuilder = DragShadowBuilder(view)
                            view.startDragAndDrop(null, dragShadowBuilder, view, 0)
                            view.visibility = INVISIBLE
                            return true
                        }
                        else -> return false
                    }
                }
                return false
            }
        }

    }

}
