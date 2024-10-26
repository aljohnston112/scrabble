package io.fourth_finger.scrabble.models

class CellAlreadyHasTileException : Exception()

class Board(
    val board: List<List<Tile?>> = List(BOARD_WIDTH_AND_HEIGHT) {
        List<Tile?>(BOARD_WIDTH_AND_HEIGHT) { null }
    },
    val wordsOnBoard: List<List<WordLocation?>> = List(BOARD_WIDTH_AND_HEIGHT) {
        List<WordLocation?>(BOARD_WIDTH_AND_HEIGHT) { null }
    }
) {

    fun addTile(tile: Tile, row: Int, col: Int): Board {
        if (board[row][col] != null) {
            throw CellAlreadyHasTileException()
        }

        val mutableBoard = board.map { it.toMutableList() }.toMutableList()
        mutableBoard[row][col] = tile
        return Board(
            mutableBoard,
            letterAdded(row, col)
        )
    }

    /**
     * Adds a letter to the board and returns a new board.
     * Both invalid an valid words are added to the new board.
     */
    private fun letterAdded(row: Int, col: Int): List<List<WordLocation?>>  {
        val dictionary = Dictionary.dictionary.definitions
        val mutableWordsOnBoard = wordsOnBoard.map { it.toMutableList() }.toMutableList()

        // Top word
        val (top, topWord) = findWordVertical(row, col)
        mutableWordsOnBoard[top][col] = WordLocation(
            Direction.DOWN,
            dictionary.containsKey(topWord)
        )

        // Left word
        val (left, leftWord) = findWordHorizontal(row, col)
        mutableWordsOnBoard[row][left] = WordLocation(
            Direction.RIGHT,
            dictionary.containsKey(leftWord)
        )

        return mutableWordsOnBoard
    }

    private fun findWordVertical(row: Int, col: Int): Pair<Int, String> {
        var top = row
        while (top > 0 && board[top - 1][col] != null) {
            top--
        }

        val wordBuilder = StringBuilder()
        var currentRow = top
        while (currentRow < BOARD_WIDTH_AND_HEIGHT && board[currentRow][col] != null) {
            wordBuilder.append(board[currentRow][col]!!.char)
            currentRow++
        }

        return Pair(top, wordBuilder.toString())
    }

    private fun findWordHorizontal(row: Int, col: Int): Pair<Int, String> {
        var left = col
        while (left > 0 && board[row][left - 1] != null) {
            left--
        }

        val wordBuilder = StringBuilder()
        var currentCol = left
        while (currentCol < BOARD_WIDTH_AND_HEIGHT && board[row][currentCol] != null) {
            wordBuilder.append(board[row][currentCol]!!.char)
            currentCol++
        }

        return Pair(left, wordBuilder.toString())
    }

    fun removeTile(row: Int, col: Int): Board {
        if (board[row][col] == null) {
            throw CellAlreadyHasTileException()
        }

        val dictionary = Dictionary.dictionary.definitions
        val mutableBoard = board.map { it.toMutableList() }.toMutableList()
        val mutableWordsOnBoard = wordsOnBoard.map { it.toMutableList() }.toMutableList()

        mutableBoard[row][col] = null
        mutableWordsOnBoard[row][col] = null

        if(row > 0) {
            val (top, topWord) = findWordVertical(row - 1, col)
            if (topWord.isNotEmpty()) {
                mutableWordsOnBoard[top][col] = WordLocation(
                    Direction.DOWN,
                    dictionary.containsKey(topWord)
                )
            }
        }

        if (row < BOARD_WIDTH_AND_HEIGHT - 1) {
            val (top, bottomWord) = findWordVertical(row + 1, col)
            if (bottomWord.isNotEmpty()) {
                mutableWordsOnBoard[top][col] = WordLocation(
                    Direction.DOWN,
                    dictionary.containsKey(bottomWord)
                )
            }
        }

        if (col > 0) {
            val (left, leftWord) = findWordHorizontal(row, col - 1)
            if (leftWord.isNotEmpty()) {
                mutableWordsOnBoard[row][left] = WordLocation(
                    Direction.RIGHT,
                    dictionary.containsKey(leftWord)
                )
            }
        }

        if (col < BOARD_WIDTH_AND_HEIGHT - 1) {
            val (left, rightWord) = findWordHorizontal(row, col + 1)
            if (rightWord.isNotEmpty()) {
                mutableWordsOnBoard[row][left] = WordLocation(
                    Direction.RIGHT,
                    dictionary.containsKey(rightWord)
                )
            }
        }

        return Board(
            mutableBoard,
            mutableWordsOnBoard
        )
    }

    companion object {
        const val BOARD_WIDTH_AND_HEIGHT = 15
    }

}