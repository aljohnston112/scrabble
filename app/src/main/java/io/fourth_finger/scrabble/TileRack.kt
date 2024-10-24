package io.fourth_finger.scrabble

class TileRack(val tiles: List<Tile>){

    init {
        require(tiles.size <= MAX_TILES) { "Tile rack must have $MAX_TILES or fewer tiles, but has ${tiles.size}" }
    }

    fun withoutTile(char: Char): TileRack {
        val index = tiles.indexOfFirst { it.char == char }
        require(index != -1) { "Tile rack does not have the tile with letter $char" }
        return TileRack(tiles.toMutableList().apply { removeAt(index) })
    }

    fun withTiles(newTiles: List<Tile>): TileRack {
        require(newTiles.size + tiles.size <= MAX_TILES) { "Tile rack can only hold 7 letters; had ${tiles.size} and ${newTiles.size} was given" }
        return TileRack(tiles.toMutableList().apply { addAll(newTiles) })
    }

    fun withTile(newTile: Tile): TileRack {
        require(1 + tiles.size <= MAX_TILES) { "Tile rack can only hold 7 letters; had ${tiles.size} and 1 was given" }
        return TileRack(tiles.toMutableList().apply { add(newTile) })
    }

    companion object{
        const val MAX_TILES = 7
    }

}