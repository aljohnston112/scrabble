package io.fourth_finger.scrabble

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.fourth_finger.scrabble.models.GameState

class GameStateContainer {

    private val _gameState = MutableLiveData<GameState>(GameState.getStartingGame())
    val gameState = _gameState as LiveData<GameState>

    fun postNewGameState(gameState: GameState){
        _gameState.postValue(gameState)
    }

}