package io.fourth_finger.scrabble.models

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class Tile(val char: Char, val points: Int){

    private val _invalidate = MutableSharedFlow<Unit>()
    val invalidate = _invalidate as SharedFlow<Unit>

    var isVerticalRed = false
        private set

    suspend fun setVerticalRed(value: Boolean) {
        if(value != isVerticalRed){
            isVerticalRed = value
            _invalidate.emit(Unit)
            Log.e("Tile", "Tile $char vertical red set to $value")
        }
    }
    var isHorizontalRed = false
        private set

    suspend fun setHorizontalRed(value: Boolean) {
        if(value != isHorizontalRed){
            isHorizontalRed = value
            _invalidate.emit(Unit)
            Log.e("Tile", "Tile $char horizontal red set to $value")
        }
    }

}

