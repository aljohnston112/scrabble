package io.fourth_finger.scrabble.models

import android.content.Context
import io.fourth_finger.scrabble.dictionary.DictionaryUtility
import io.fourth_finger.scrabble.dictionary.WordTree
import kotlin.random.Random

class GameState(
    val board: Board = Board(),
    val tileBag: TileBag = TileBag.getStartingBag(),
    val tileRack: TileRack = TileRack(emptyList()),
    val opponentTileRack: TileRack = TileRack(emptyList())
) {

    fun getPlayerChars(): List<Char> {
        return tileRack.tiles.map { it.char }
    }

    companion object{

        var dictionary = emptyList<WordTree>()
            private set

        fun loadDictionary(context: Context){
            dictionary = DictionaryUtility.Companion.getDictionary(context)
        }

        fun getStartingGame(): GameState {
            return afterFillingRacks(GameState())
        }

        fun afterFillingRacks(gameState: GameState): GameState {
            val numToAdd = TileRack.Companion.MAX_TILES - gameState.tileRack.tiles.size
            val numToAddToOpponent = TileRack.Companion.MAX_TILES - gameState.opponentTileRack.tiles.size
            if (numToAdd <= 0 && numToAddToOpponent <= 0) return gameState
            val availableTilesInBag = gameState.tileBag.tiles.toMutableList()

            val tilesToAddToRack = mutableListOf<Tile>()
            if (numToAdd > 0) {
                repeat(numToAdd) {
                    if (availableTilesInBag.isNotEmpty()) {
                        tilesToAddToRack.add(
                            availableTilesInBag.removeAt(
                                Random.nextInt(availableTilesInBag.size)
                            )
                        )
                    }
                }
            }

            val tilesToAddToOpponentRack = mutableListOf<Tile>()
            if (numToAddToOpponent > 0) {
                repeat(numToAddToOpponent) {
                    if (availableTilesInBag.isNotEmpty()) {
                        {
                            tilesToAddToOpponentRack.add(
                                availableTilesInBag.removeAt(
                                    Random.nextInt(availableTilesInBag.size)
                                )
                            )
                        }
                    }
                }
            }

            return GameState(
                gameState.board,
                TileBag(availableTilesInBag),
                gameState.tileRack.withTiles(tilesToAddToRack),
                gameState.opponentTileRack.withTiles(tilesToAddToOpponentRack)
            )
        }

    }

}