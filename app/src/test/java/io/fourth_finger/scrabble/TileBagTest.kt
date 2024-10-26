package io.fourth_finger.scrabble

import io.fourth_finger.scrabble.models.Tile
import io.fourth_finger.scrabble.models.TileBag.Companion.getStartingBag
import org.junit.Test

import org.junit.Assert.*

class TileBagTest {

    @Test
    fun `getStartingBag should return a TileBag with correct tiles and counts`() {
        val tileBag = getStartingBag()

        // Create a map of the expected tile counts
        val expectedTileCounts = mapOf(
            'E' to 12,
            'A' to 9, 'I' to 9,
            'O' to 8,
            'N' to 6, 'R' to 6, 'T' to 6,
            'L' to 4, 'S' to 4, 'U' to 4, 'D' to 4,
            'G' to 3,
            ' ' to 2, 'B' to 2, 'C' to 2, 'M' to 2, 'P' to 2, 'F' to 2, 'H' to 2, 'V' to 2, 'W' to 2, 'Y' to 2,
            'K' to 1, 'J' to 1, 'X' to 1, 'Q' to 1, 'Z' to 1
        )

        val actualTileCounts = tileBag.tiles.groupingBy {tile: Tile -> tile.char }.eachCount()

        assertEquals(expectedTileCounts, actualTileCounts)
    }

}