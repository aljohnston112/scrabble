package io.fourth_finger.scrabble

import io.fourth_finger.scrabble.models.GameState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class GameStateContainer {

    private val _gameStateChanged = MutableSharedFlow<Unit>()
    val gameStateChanged = _gameStateChanged as SharedFlow<Unit>

    var gameState = GameState.getStartingGame()
        private set

    fun updateGameState(gameState: GameState){
        this.gameState = gameState
        _gameStateChanged.tryEmit(Unit)
    }

}