package io.fourth_finger.scrabble.views

import android.view.ViewGroup
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DragMonitor {

    private var isDraggingTile = false
    private lateinit var initialParent: ViewGroup

    fun isDragInProgress(): Boolean {
        return isDraggingTile
    }

    /**
     * Must only be called by the gameAreaConstraintLayout's onDragListener
     */
    fun setDragInProgress(dragInProgress: Boolean) {
        isDraggingTile = dragInProgress
    }

    fun getInitialParent(): ViewGroup {
        return initialParent
    }

    /**
     * Must only be called by the gameAreaConstraintLayout's onDragListener
     */
    fun setInitialParent(parent: ViewGroup) {
        initialParent = parent
    }

}