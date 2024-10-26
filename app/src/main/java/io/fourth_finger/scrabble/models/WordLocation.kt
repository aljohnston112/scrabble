package io.fourth_finger.scrabble.models

enum class Direction{
    DOWN,
    RIGHT,
    UNKNOWN
}

class WordLocation(
    val direction: Direction,
    val isValid: Boolean
)