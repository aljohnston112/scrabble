package io.fourth_finger.scrabble

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class TileRackViewGroup(
    context: Context
): ViewGroup(context) {

    private val numberOfTiles = 7

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        val squareSize = (r - l) / numberOfTiles

        for (row in 0 until numberOfTiles) {
                if (row < childCount) {
                    val child = getChildAt(row)

                    val childLeft = row * squareSize

                    child.layout(
                        childLeft,
                        0,
                        childLeft + squareSize,
                        squareSize
                    )

            }
        }
    }

}