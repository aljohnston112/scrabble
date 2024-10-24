package io.fourth_finger.scrabble

class Board {

    val board = Array(BOARD_WIDTH_AND_HEIGHT) { Array<Tile?>(BOARD_WIDTH_AND_HEIGHT) { null } }

    companion object{
        const val BOARD_WIDTH_AND_HEIGHT = 15
    }

}