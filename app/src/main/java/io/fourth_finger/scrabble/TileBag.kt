package io.fourth_finger.scrabble

import io.fourth_finger.scrabble.TileRack.Companion.MAX_TILES
import kotlin.random.Random

class TileBag(val tiles: List<Tile>) {

    fun afterFillingRack(tileRack: TileRack): TileBag {
        val numToAdd = MAX_TILES - tileRack.tiles.size
        if (numToAdd <= 0) return this

        val availableTiles = tiles.toMutableList()
        val tilesToAdd = mutableListOf<Tile>()
        repeat(numToAdd) {
            if (availableTiles.isNotEmpty()) {
                tilesToAdd.add(
                    availableTiles.removeAt(
                        Random.nextInt(availableTiles.size)
                    )
                )
            }
        }

        return TileBag(availableTiles)
    }

    companion object {

        fun getStartingBag(): TileBag {
            return TileBag(
                mutableListOf<Tile>().apply {
                    addAll(List(2) { Tile(' ', 0) })

                    addAll(List(12) { Tile('E', 1) })
                    addAll(List(9) { Tile('A', 1) })
                    addAll(List(9) { Tile('I', 1) })
                    addAll(List(8) { Tile('O', 1) })
                    addAll(List(6) { Tile('N', 1) })
                    addAll(List(6) { Tile('R', 1) })
                    addAll(List(6) { Tile('T', 1) })
                    addAll(List(4) { Tile('L', 1) })
                    addAll(List(4) { Tile('S', 1) })
                    addAll(List(4) { Tile('U', 1) })

                    addAll(List(4) { Tile('D', 2) })
                    addAll(List(3) { Tile('G', 2) })

                    addAll(List(2) { Tile('B', 3) })
                    addAll(List(2) { Tile('C', 3) })
                    addAll(List(2) { Tile('M', 3) })
                    addAll(List(2) { Tile('P', 3) })

                    addAll(List(2) { Tile('F', 4) })
                    addAll(List(2) { Tile('H', 4) })
                    addAll(List(2) { Tile('V', 4) })
                    addAll(List(2) { Tile('W', 4) })
                    addAll(List(2) { Tile('Y', 4) })

                    addAll(List(1) { Tile('K', 5) })

                    addAll(List(1) { Tile('J', 8) })
                    addAll(List(1) { Tile('X', 8) })

                    addAll(List(1) { Tile('Q', 10) })
                    addAll(List(1) { Tile('Z', 10) })
                }
            )
        }

    }

}