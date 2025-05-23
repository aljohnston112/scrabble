package io.fourth_finger.scrabble.views

import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import io.fourth_finger.scrabble.models.TileRack
import io.fourth_finger.scrabble.views.ViewUtil.Companion.removeFromParent
import kotlinx.coroutines.runBlocking
import kotlin.math.floor
import kotlin.math.min
import kotlin.properties.Delegates


class TileRackViewGroup(
    context: Context,
    tileRack: TileRack,
    val dragMonitor: DragMonitor,
) : ViewGroup(context) {

    private val numberOfTiles = 7
    private var squareSize by Delegates.notNull<Int>()


    private val tileDragListener = object : OnDragListener {

        private var indexOfDraggedChild = -1

        override fun onDrag(v: View, event: DragEvent): Boolean {
            val view = event.localState
            if (view is TileView) {
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        Log.d("TileRack", "Drag started")
                        if (!dragMonitor.isDragInProgress()) {
                            indexOfDraggedChild = indexOfChild(view)
                            return true
                        } else {
                            return false
                        }
                    }

                    DragEvent.ACTION_DROP -> {
                        Log.d("TileRack", "Drag drop")
                        view.removeFromParent()
                        view.isVisible = true
                        view.translationX = 0f
                        view.translationY = 0f
                        view.scaleX = 1f
                        view.scaleY = 1f
                        val index = min(floor(event.x / squareSize).toInt(), childCount)
                        addView(view, index)
                        Log.d("TileRack", "Drag drop clearing red")
                        view.post {
                            runBlocking {
                                view.tile?.setHorizontalRed(false)
                                view.tile?.setVerticalRed(false)
                            }
                        }
                        Log.d("TileRack", "Drag drop finished")
                        return true
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        Log.d("TileRack", "Drag end")

                        val initialParent = dragMonitor.getInitialParent()
                        if (
                            initialParent is TileRackViewGroup &&
                            (view.parent == null ||
                                    view.parent is TileRackViewGroup) // Tile may have been rejected by the game board
                        ) {
                            Log.d("TileRack", "Drag end from rack to void or rack")

                            // Puts back tiles that came from the tile rack and went into to the void
                            view.isVisible = true
                            view.translationX = 0f
                            view.translationY = 0f
                            view.scaleX = 1f
                            view.scaleY = 1f
                            if (view.parent == null) {
                                addView(view, indexOfDraggedChild)
                            }
                            return true
                        }
                        // requestLayout()
                        // invalidate()
                        Log.d("TileRack", "Drag end finished")
                        return false
                    }
                }
            }
            return false
        }
    }

    private val touchListener = object : OnTouchListener {

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            if (view is TileView) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (!dragMonitor.isDragInProgress()) {
                            val dragShadowBuilder = DragShadowBuilder(view)
                            view.startDragAndDrop(null, dragShadowBuilder, view, 0)
                            view.visibility = INVISIBLE
                            return true
                        } else {
                            return false
                        }
                    }

                    else -> return false
                }
            }
            return false
        }
    }

    init {
        addTileRack(tileRack)
    }

    private fun addTileRack(tileRack: TileRack) {
        for (tile in tileRack.tiles) {
            val view = TileView(context, tile)
            view.id = generateViewId()
            view.setOnTouchListener(touchListener)
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
        addTileRack(rack)
    }

}