package io.fourth_finger.scrabble

import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.floor
import kotlin.properties.Delegates


class TileRackViewGroup(
    context: Context,
    tileRack: TileRack
) : ViewGroup(context) {

    private val numberOfTiles = 7
    private var squareSize by Delegates.notNull<Int>()

    private val isDraggingTile = AtomicBoolean(false)

    private val tileDragListener = object : OnDragListener {

        private val dropped = AtomicBoolean(false)
        private var indexOfDraggingChild = -1

        override fun onDrag(v: View, event: DragEvent): Boolean {
            val view = event.localState
            if (view is TileView) {
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        Log.d("TileRack", "Drag started")
                        if (!isDraggingTile.get()) {
                            isDraggingTile.set(true)
                            indexOfDraggingChild = indexOfChild(view)
                            return true
                        } else {
                            return false
                        }
                    }

                    DragEvent.ACTION_DROP -> {
                        Log.d("TileRack", "Drag drop")
                        val parent = view.parent as ViewGroup
                        parent.removeView(view)
                        view.isVisible = true
                        view.translationX = 0f
                        view.translationY = 0f
                        view.scaleX = 1f
                        view.scaleY = 1f
                        val index = floor(event.x / squareSize).toInt()
                        addView(view, index)
                        dropped.set(true)
                        requestLayout()
                        return false
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        Log.d("TileRack", "Drag end")
                        isDraggingTile.set(false)

                        if (dropped.get()) {
                            dropped.set(false)
                            return false
                        }

                        val parent = view.parent as ViewGroup
                        if (parent is GameBoardViewGroup) {
                            return false
                        }

                        parent.removeView(view)
                        view.isVisible = true
                        view.translationX = 0f
                        view.translationY = 0f
                        view.scaleX = 1f
                        view.scaleY = 1f
                        addView(view, indexOfDraggingChild)
                        requestLayout()
                        return true
                    }

                    else -> return false
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
                        if (!isDraggingTile.get()) {
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
            view.setOnTouchListener(touchListener)
            view.id = generateViewId()
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