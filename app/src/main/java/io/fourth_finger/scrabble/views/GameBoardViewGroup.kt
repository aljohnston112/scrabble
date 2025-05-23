package io.fourth_finger.scrabble.views

import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import io.fourth_finger.scrabble.GameStateContainer
import io.fourth_finger.scrabble.models.Board
import io.fourth_finger.scrabble.models.Direction
import io.fourth_finger.scrabble.models.GameState
import kotlinx.coroutines.runBlocking
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min


class GameBoardViewGroup(
    context: Context,
    private var gameStateContainer: GameStateContainer,
    val dragMonitor: DragMonitor,
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

    private val dragListener = object : OnDragListener {

        private var targetX = 0
        private var targetY = 0
        private var targetColumn = 0
        private var targetRow = 0

        override fun onDrag(v: View?, event: DragEvent): Boolean {
            val view = event.localState
            if (view is TileView) {
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        Log.d("GameBoard", "Drag started")
                        return !dragMonitor.isDragInProgress()
                    }

                    DragEvent.ACTION_DROP -> {
                        Log.d("GameBoard", "Drag dropped")

                        // Initial location on board
                        if (view.layoutParams !is MarginLayoutParams) {
                            view.layoutParams = MarginLayoutParams(gridSize, gridSize)
                        }
                        val (initialCellX, initialCellY) = getBoardCoordinates(view)

                        // Calculate row and column based on drawing size and location
                        val adjustedDropX = (event.x - currentTranslationX) / scaleFactor
                        val adjustedDropY = (event.y - currentTranslationY) / scaleFactor
                        targetColumn = floor(adjustedDropX / squareSize).toInt()
                        targetRow = floor(adjustedDropY / squareSize).toInt()
                        val gameState = gameStateContainer.gameState

                        val spaceAlreadyHasTile =
                            gameState.gameBoard.board[targetRow][targetColumn] != null
                        if (spaceAlreadyHasTile) {
                            return false
                        }

                        // Margins are based on layout size
                        targetX = (targetColumn * squareSize)
                        targetY = (targetRow * squareSize)
                        val parent = view.parent as ViewGroup
                        parent.removeView(view)
                        addView(view)
                        view.visibility = VISIBLE
                        (view.layoutParams as MarginLayoutParams).leftMargin = targetX
                        (view.layoutParams as MarginLayoutParams).topMargin = targetY

                        val initialParent = dragMonitor.getInitialParent()
                        if (initialParent is TileRackViewGroup) {
                            Log.d("GameBoard", "Drag dropped from tile rack to board")
                            gameStateContainer.updateGameState(
                                GameState(
                                    gameState.gameBoard.afterAddingTile(
                                        view.tile!!,
                                        targetRow,
                                        targetColumn
                                    ),
                                    gameState.tileBag,
                                    gameState.tileRack.withoutTile(view.tile),
                                    gameState.opponentTileRack
                                )
                            )
                        } else if (initialParent is GameBoardViewGroup) {
                            Log.d("GameBoard", "Drag dropped from board to board")
                            gameStateContainer.updateGameState(
                                GameState(
                                    gameState.gameBoard.removeTile(
                                        initialCellX,
                                        initialCellY
                                    ).afterAddingTile(
                                        view.tile!!,
                                        targetRow,
                                        targetColumn
                                    ),
                                    gameState.tileBag,
                                    gameState.tileRack,
                                    gameState.opponentTileRack
                                )
                            )
                        }
                        Log.d("GameBoard", "Drag dropped finished")
                        return true
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        Log.d("GameBoard", "Drag ended")
                        val initialParent = dragMonitor.getInitialParent()
                        if (initialParent is GameBoardViewGroup && view.parent is TileRackViewGroup) {
                            Log.d("GameBoard", "Drag end from board to rack")
                            val gameState = gameStateContainer.gameState
                            val (row, column) = getBoardCoordinates(view)
                            gameStateContainer.updateGameState(
                                GameState(
                                    gameState.gameBoard.removeTile(
                                        row,
                                        column
                                    ),
                                    gameState.tileBag,
                                    gameState.tileRack.withTile(view.tile!!),
                                    gameState.opponentTileRack
                                )
                            )
                        } else if (
                            initialParent is GameBoardViewGroup && view.parent == null
                        ) {
                            Log.d("GameBoard", "Drag end from board to void")
                            addView(view)
                            view.visibility = VISIBLE
                            view.layoutParams = MarginLayoutParams(gridSize, gridSize)
                            (view.layoutParams as MarginLayoutParams).leftMargin = targetX
                            (view.layoutParams as MarginLayoutParams).topMargin = targetY
                        } else if (
                            initialParent is GameBoardViewGroup && view.parent is GameBoardViewGroup
                        ) {
                            Log.d("GameBoard", "Drag end from board to board")
                            view.visibility = VISIBLE
                        }
                        requestLayout()
                        invalidate()
                        Log.d("GameBoard", "Drag ended finished")
                        return true
                    }
                }
            }
            return false
        }
    }

    init {
        setOnDragListener(dragListener)
    }

    private fun getBoardCoordinates(view: TileView): Pair<Int, Int> {
        val column =
            ((view.layoutParams as MarginLayoutParams).leftMargin) / squareSize
        val row =
            ((view.layoutParams as MarginLayoutParams).topMargin) / squareSize
        return Pair(row, column)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.e("Game board", "Laying out board")

        // The grid itself
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val index = row * gridSize + col
                val child = getChildAt(index)

                val childLeft = (col * squareSize)
                val childTop = (row * squareSize)

                child.layout(
                    childLeft,
                    childTop,
                    childLeft + squareSize,
                    childTop + squareSize
                )
            }
        }

        val gameBoard = gameStateContainer.gameState.gameBoard
        for (i in gridSize * gridSize until childCount) {
            val child = getChildAt(i)
            val (row, column) = getBoardCoordinates(child as TileView)
            var tile = gameBoard.board[row][column]
            if (tile != null) {
                runBlocking {
                    tile.setVerticalRed(false)
                    tile.setHorizontalRed(false)
                }
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
            val (row, column) = getBoardCoordinates(child as TileView)
            val wordLocation = gameBoard.wordsOnBoard[row][column]
            if (wordLocation != null) {
                val valid = wordLocation.isValid
                val isVerticalRed =
                    !valid && (wordLocation.direction == Direction.DOWN || wordLocation.direction == Direction.BOTH)
                var tile = gameBoard.board[row][column]
                var currentRow = row - 1
                while (tile != null) {
                    currentRow++
                    tile = gameBoard.board[currentRow][column]
                    runBlocking {
                        tile?.setVerticalRed(isVerticalRed)
                    }
                }

                val isHorizontalRed =
                    !valid && (wordLocation.direction == Direction.RIGHT || wordLocation.direction == Direction.BOTH)
                tile = gameBoard.board[row][column]
                var currentColumn = column - 1
                while (tile != null) {
                    currentColumn++
                    tile = gameBoard.board[row][currentColumn]
                    runBlocking {
                        tile?.setHorizontalRed(isHorizontalRed)
                    }
                }
            }

        }
        updateChildren()
        Log.e("Game board", "Done laying out board")
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
