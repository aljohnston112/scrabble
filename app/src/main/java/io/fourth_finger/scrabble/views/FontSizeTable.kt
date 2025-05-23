package io.fourth_finger.scrabble.views

import android.graphics.Paint
import android.graphics.Rect
import kotlin.math.max

class FontSizeUtility() {

    companion object {

        fun getLargestFontThatFitsSquare(letter: String, squareSize: Int): Float {
            if(letter == " "){
                return 0f
            }

            var fontWidth: Int = -1
            val paint = Paint()
            paint.textSize = 1f

            val bounds = Rect()
            while (fontWidth < squareSize) {
                paint.getTextBounds(letter, 0, letter.length, bounds)
                fontWidth = max(bounds.height(), bounds.width())
                paint.textSize++
            }
            return --paint.textSize
        }

    }

}