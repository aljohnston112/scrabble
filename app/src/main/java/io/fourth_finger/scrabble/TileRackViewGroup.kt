package io.fourth_finger.scrabble

import android.content.Context
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

class TileRackViewGroup(
    context: Context,
    tileRack: TileRack
) : ViewGroup(context) {

    private val numberOfTiles = 7
    private var squareSize by Delegates.notNull<Int>()

    private val tileDragListener = object : OnDragListener {


        override fun onDrag(v: View, event: DragEvent): Boolean {
            val view = event.localState
            if (view is TileView) {
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        return true
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
                        return true
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        return true
                    }

                    else -> return false
                }
            }
            return false
        }
    }

    init {
        for (tile in tileRack.tiles) {
            val view = TileView(context, tile)
            view.isMovable = true
            addView(view)
        }
        setOnDragListener(tileDragListener)
    }

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

    fun addNewTileRack(rack: TileRack) {
        removeAllViews()
        for (tile in rack.tiles) {
            val view = TileView(context, tile)
            view.isMovable = true
            addView(view)
        }
    }

}