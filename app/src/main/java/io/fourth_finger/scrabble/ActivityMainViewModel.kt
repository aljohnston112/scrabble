package io.fourth_finger.scrabble

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityMainViewModel: ViewModel() {

    private var gameState = GameState.getStartingGame()

    private val _board = MutableLiveData(gameState.board)
    val board = _board as LiveData<Board>

    private val _tileRack = MutableLiveData(gameState.tileRack)
    val tileRack = _tileRack as LiveData<TileRack>

}