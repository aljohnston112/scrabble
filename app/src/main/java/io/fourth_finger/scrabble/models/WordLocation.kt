package io.fourth_finger.scrabble.models

enum class Direction{
    DOWN,
    RIGHT,
    BOTH
}

class WordLocation(
    val direction: Direction,
    val isValid: Boolean
)