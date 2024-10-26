package io.fourth_finger.scrabble.views

import android.content.Context
import android.view.DragEvent
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import io.fourth_finger.scrabble.GameStateContainer
import io.fourth_finger.scrabble.models.Board
import io.fourth_finger.scrabble.models.GameState
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min


class GameBoardViewGroup(
    context: Context,
    private var gameStateContainer: GameStateContainer,
    val squareSize: Int = 192
) : ViewGroup(context) {

    private val gridSize = Board.BOARD_WIDTH_AND_HEIGHT

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
            val lastScaleFactor = scaleFactor
            currentTranslationX /= lastScaleFactor
            currentTranslationY /= lastScaleFactor
            scaleFactor *= detector.scaleFactor
            scaleFactor = min(scaleFactor, 1.5f)

            // Limit scale to the smaller of the width or height
            val windowManager =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
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

            // Update the translation
            currentTranslationX *= scaleFactor
            currentTranslationY *= scaleFactor

            currentTranslationX = when {
                windowWidth <= width -> 0f
                currentTranslationX > 0 -> 0f
                currentTranslationX < (width - windowWidth) -> (width - windowWidth).toFloat()
                else -> currentTranslationX
            }

            currentTranslationY = when {
                windowHeight <= height -> 0f
                currentTranslationY > 0 -> 0f
                currentTranslationY < (height - windowHeight) -> (height - windowHeight).toFloat()
                else -> currentTranslationY
            }

            updateChildren()

            return true
        }

    }

    private val isDraggingTile = AtomicBoolean(false)

    private val dragListener = object : OnDragListener {

        private val dropped = AtomicBoolean(false)
        private var targetX = 0
        private var targetY = 0
        private var targetCellX = 0f
        private var targetCellY = 0f

        override fun onDrag(v: View?, event: DragEvent): Boolean {
            val view = event.localState
            if (view is TileView) {
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        isDraggingTile.set(true)
                        return true
                    }

                    DragEvent.ACTION_DROP -> {
                        isDraggingTile.set(true)
                        dropped.set(true)
                        // Calculate row and column based on drawing size
                        val adjustedDropX = (event.x - currentTranslationX) / scaleFactor
                        val adjustedDropY = (event.y - currentTranslationY) / scaleFactor
                        targetCellX = floor(adjustedDropX / squareSize)
                        targetCellY = floor(adjustedDropY / squareSize)

                        // Margins are based on layout size
                        targetX = (targetCellX * squareSize).toInt()
                        targetY = (targetCellY * squareSize).toInt()
                        val parent = view.parent as ViewGroup
                        parent.removeView(view)
                        addView(view)
                        view.visibility = VISIBLE
                        view.layoutParams = MarginLayoutParams(gridSize, gridSize)
                        (view.layoutParams as MarginLayoutParams).leftMargin = targetX
                        (view.layoutParams as MarginLayoutParams).topMargin = targetY

                        val gameState = gameStateContainer.gameState.value!!
                        gameStateContainer.postNewGameState(
                            GameState(
                                gameState.board.addTile(
                                    view.tile!!,
                                    targetCellX.toInt(),
                                    targetCellY.toInt()
                                ),
                                gameState.tileBag,
                                gameState.tileRack.withoutTile(view.tile),
                                gameState.opponentTileRack
                            )
                        )
                        requestLayout()
                        return true
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        if (!dropped.get()) {
                            dropped.set(false)
                            return false
                        }

                        val parent = view.parent as ViewGroup
                        if (parent is TileRackViewGroup) {
                            val gameState = gameStateContainer.gameState.value!!
                            gameStateContainer.postNewGameState(
                                GameState(
                                    gameState.board.removeTile(
                                        targetCellX.toInt(),
                                        targetCellY.toInt()
                                    ),
                                    gameState.tileBag,
                                    gameState.tileRack.withTile(view.tile!!),
                                    gameState.opponentTileRack
                                )
                            )
                            return false
                        }

                        parent.removeView(view)
                        addView(view)
                        view.visibility = VISIBLE
                        view.layoutParams = MarginLayoutParams(gridSize, gridSize)
                        (view.layoutParams as MarginLayoutParams).leftMargin = targetX.toInt()
                        (view.layoutParams as MarginLayoutParams).topMargin = targetY.toInt()
                        requestLayout()

                        return true
                    }

                    else -> return false
                }
            }
            return false
        }
    }


    init {
        setOnDragListener(dragListener)
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

}
