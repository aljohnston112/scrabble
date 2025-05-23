package io.fourth_finger.scrabble.dictionary

class LetterNode private constructor(
    val char: Char,
    val score: Int
){

    override fun toString(): String {
        return "$char: $score"
    }

    companion object {
        private val SPACE = LetterNode(' ', 0)

        private val E = LetterNode('E', 1)
        private val A = LetterNode('A', 1)
        private val I = LetterNode('I', 1)
        private val O = LetterNode('O', 1)
        private val N = LetterNode('N', 1)
        private val R = LetterNode('R', 1)
        private val T = LetterNode('T', 1)
        private val L = LetterNode('L', 1)
        private val S = LetterNode('S', 1)
        private val U = LetterNode('U', 1)

        private val D = LetterNode('D', 2)
        private val G = LetterNode('G', 2)

        private val B = LetterNode('B', 3)
        private val C = LetterNode('C', 3)
        private val M = LetterNode('M', 3)
        private val P = LetterNode('P', 3)

        private val F = LetterNode('F', 4)
        private val H = LetterNode('H', 4)
        private val V = LetterNode('V', 4)
        private val W = LetterNode('W', 4)
        private val Y = LetterNode('Y', 4)

        private val K = LetterNode('K', 5)

        private val J = LetterNode('J', 8)
        private val X = LetterNode('X', 8)

        private val Q = LetterNode('Q', 10)
        private val Z = LetterNode('Z', 10)

        val letterNodeMap: Map<Char, LetterNode> = mapOf(
            ' ' to SPACE,
            'E' to E, 'A' to A, 'I' to I, 'O' to O, 'N' to N,
            'R' to R, 'T' to T, 'L' to L, 'S' to S, 'U' to U,
            'D' to D, 'G' to G,
            'B' to B, 'C' to C, 'M' to M, 'P' to P,
            'F' to F, 'H' to H, 'V' to V, 'W' to W, 'Y' to Y,
            'K' to K,
            'J' to J, 'X' to X,
            'Q' to Q, 'Z' to Z
        )

        fun createRoot(): WordTree {
            return WordTree(LetterNode('.', 0))
        }

    }

}