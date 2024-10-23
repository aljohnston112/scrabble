package io.fourth_finger.scrabble

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlin.math.floor
import kotlin.properties.Delegates

class TileRackViewGroup(
    context: Context
): ViewGroup(context) {

    private val numberOfTiles = 7
    private var squareSize by Delegates.notNull<Int>()

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        squareSize = (r - l) / numberOfTiles

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

    init {
        setOnDragListener { _, event ->
            val view = event.localState
            if (view is TileView) {
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        return@setOnDragListener true
                    }

                    DragEvent.ACTION_DROP -> {
                        val parent = view.parent as ViewGroup
                        parent.removeView(view)
                        view.isVisible = true
                        view.translationX = 0f
                        view.translationY = 0f
                        view.scaleX = 1f
                        view.scaleY = 1f
                        addView(view)
                        requestLayout()
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

}