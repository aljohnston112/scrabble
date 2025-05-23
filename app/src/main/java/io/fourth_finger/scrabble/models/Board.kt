package io.fourth_finger.scrabble.models

class BoardTileException : Exception()

class Board(
    val board: List<List<Tile?>> = List(BOARD_WIDTH_AND_HEIGHT) {
        List<Tile?>(BOARD_WIDTH_AND_HEIGHT) { null }
    },
    val wordsOnBoard: List<List<WordLocation?>> = List(BOARD_WIDTH_AND_HEIGHT) {
        List<WordLocation?>(BOARD_WIDTH_AND_HEIGHT) { null }
    }
) {

    fun afterAddingTile(
        tile: Tile,
        row: Int,
        col: Int
    ): Board {
        if (board[row][col] != null) {
            throw BoardTileException()
        }

        val mutableBoard = board.map { it.toMutableList() }.toMutableList()
        mutableBoard[row][col] = tile
        return Board(
            mutableBoard,
            getWordsAfterLetterAdded(
                row,
                col,
                mutableBoard
            )
        )
    }

    /**
     * Adds a letter to the board and returns a new board.
     * Both invalid an valid words are added to the new board.
     */
    private fun getWordsAfterLetterAdded(
        row: Int,
        col: Int,
        board: List<List<Tile?>>
    ): List<List<WordLocation?>> {
        val dictionary = Dictionary.dictionary.definitions
        val mutableWordsOnBoard = wordsOnBoard.map { it.toMutableList() }.toMutableList()

        // Top word
        val (top, topWord) = findWordVertical(
            row,
            col,
            board
        )
        if(topWord.length > 1) {
            if (mutableWordsOnBoard[top][col]?.direction == Direction.RIGHT) {
                mutableWordsOnBoard[top][col] = WordLocation(
                    Direction.BOTH,
                    dictionary.containsKey(topWord)
                )
            } else {
                mutableWordsOnBoard[top][col] = WordLocation(
                    Direction.DOWN,
                    dictionary.containsKey(topWord)
                )
            }
        }

        // Left word
        val (left, leftWord) = findWordHorizontal(
            row,
            col,
            board
        )
        if(leftWord.length > 1) {
            if (mutableWordsOnBoard[row][left]?.direction == Direction.DOWN) {
                mutableWordsOnBoard[row][left] = WordLocation(
                    Direction.BOTH,
                    dictionary.containsKey(leftWord)
                )
            } else {
                mutableWordsOnBoard[row][left] = WordLocation(
                    Direction.RIGHT,
                    dictionary.containsKey(leftWord)
                )
            }
        }

        return mutableWordsOnBoard
    }

    private fun findWordVertical(
        row: Int,
        col: Int,
        board: List<List<Tile?>>
    ): Pair<Int, String> {
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

        return Pair(
            top,
            wordBuilder.toString()
        )
    }

    private fun findWordHorizontal(
        row: Int,
        col: Int,
        board: List<List<Tile?>>
    ): Pair<Int, String> {
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

        return Pair(
            left,
            wordBuilder.toString()
        )
    }

    fun removeTile(
        row: Int,
        col: Int
    ): Board {
        if (board[row][col] == null) {
            throw BoardTileException()
        }

        val dictionary = Dictionary.dictionary.definitions
        val mutableBoard = board.map { it.toMutableList() }.toMutableList()
        val mutableWordsOnBoard = wordsOnBoard.map { it.toMutableList() }.toMutableList()

        mutableBoard[row][col] = null
        mutableWordsOnBoard[row][col] = null

        if (row > 0) {
            val (top, topWord) = findWordVertical(
                row - 1,
                col,
                mutableBoard
            )
            if (topWord.length > 1) {
                if (mutableWordsOnBoard[top][col]?.direction == Direction.RIGHT) {
                    mutableWordsOnBoard[top][col] = WordLocation(
                        Direction.BOTH,
                        dictionary.containsKey(topWord)
                    )
                } else {
                    mutableWordsOnBoard[top][col] = WordLocation(
                        Direction.DOWN,
                        dictionary.containsKey(topWord)
                    )
                }
            } else {
                if (mutableWordsOnBoard[top][col]?.direction == Direction.BOTH) {
                    mutableWordsOnBoard[top][col] = WordLocation(
                        Direction.RIGHT,
                        mutableWordsOnBoard[top][col]!!.isValid
                    )
                } else if(mutableWordsOnBoard[top][col]?.direction != Direction.RIGHT){
                   mutableWordsOnBoard[top][col] = null
                }
            }
        }

        if (row < BOARD_WIDTH_AND_HEIGHT - 1) {
            val (top, bottomWord) = findWordVertical(
                row + 1,
                col,
                mutableBoard
            )
            if (bottomWord.length > 1) {
                if (mutableWordsOnBoard[top][col]?.direction == Direction.RIGHT) {
                    mutableWordsOnBoard[top][col] = WordLocation(
                        Direction.BOTH,
                        dictionary.containsKey(bottomWord)
                    )
                } else {
                    mutableWordsOnBoard[top][col] = WordLocation(
                        Direction.DOWN,
                        dictionary.containsKey(bottomWord)
                    )
                }
            } else {
                if (mutableWordsOnBoard[top][col]?.direction == Direction.BOTH) {
                    mutableWordsOnBoard[top][col] = WordLocation(
                        Direction.RIGHT,
                        mutableWordsOnBoard[top][col]!!.isValid
                    )
                } else if(mutableWordsOnBoard[top][col]?.direction != Direction.RIGHT){
                    mutableWordsOnBoard[top][col] = null
                }
            }
        }

        if (col > 0) {
            val (left, leftWord) = findWordHorizontal(
                row,
                col - 1,
                mutableBoard
            )
            if (leftWord.length > 1) {
                if (mutableWordsOnBoard[row][left]?.direction == Direction.DOWN) {
                    mutableWordsOnBoard[row][left] = WordLocation(
                        Direction.BOTH,
                        dictionary.containsKey(leftWord)
                    )
                } else {
                    mutableWordsOnBoard[row][left] = WordLocation(
                        Direction.RIGHT,
                        dictionary.containsKey(leftWord)
                    )
                }
            } else {
                if (mutableWordsOnBoard[row][left]?.direction == Direction.BOTH) {
                    mutableWordsOnBoard[row][left] = WordLocation(
                        Direction.DOWN,
                        mutableWordsOnBoard[row][left]!!.isValid
                    )
                } else if(mutableWordsOnBoard[row][left]?.direction != Direction.DOWN){
                    mutableWordsOnBoard[row][left] = null
                }
            }
        }

        if (col < BOARD_WIDTH_AND_HEIGHT - 1) {
            val (left, rightWord) = findWordHorizontal(
                row,
                col + 1,
                mutableBoard
            )
            if (rightWord.length > 1) {
                if (mutableWordsOnBoard[row][left]?.direction == Direction.DOWN) {
                    mutableWordsOnBoard[row][left] = WordLocation(
                        Direction.BOTH,
                        dictionary.containsKey(rightWord)
                    )
                } else {
                    mutableWordsOnBoard[row][left] = WordLocation(
                        Direction.RIGHT,
                        dictionary.containsKey(rightWord)
                    )
                }
            }else {
                if (mutableWordsOnBoard[row][left]?.direction == Direction.BOTH) {
                    mutableWordsOnBoard[row][left] = WordLocation(
                        Direction.DOWN,
                        mutableWordsOnBoard[row][left]!!.isValid
                    )
                } else if(mutableWordsOnBoard[row][left]?.direction != Direction.DOWN){
                    mutableWordsOnBoard[row][left] = null
                }
            }
        }

        return Board(
            mutableBoard,
            mutableWordsOnBoard
        )
    }

    fun hasWords(): Boolean {
        for (tileList in board){
            for(tile in tileList){
                if(tile != null){
                    return true
                }
            }
        }
        return false
    }

    companion object {
        const val BOARD_WIDTH_AND_HEIGHT = 15

        fun checkUserPlay(initialBoard: Board, newBoard: Board) {

        }

    }

}

class CheckUserPlayResult(val isValid: Boolean, val x: Int, val y: Int, isHorizontal: Boolean)