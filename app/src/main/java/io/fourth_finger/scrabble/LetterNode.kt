package io.fourth_finger.scrabble

class LetterNode private constructor(val char: Char, val score: Int){

    override fun toString(): String {
        return "$char: $score"
    }

    companion object {
        val SPACE = LetterNode(' ', 0)

        val E = LetterNode('E', 1)
        val A = LetterNode('A', 1)
        val I = LetterNode('I', 1)
        val O = LetterNode('O', 1)
        val N = LetterNode('N', 1)
        val R = LetterNode('R', 1)
        val T = LetterNode('T', 1)
        val L = LetterNode('L', 1)
        val S = LetterNode('S', 1)
        val U = LetterNode('U', 1)

        val D = LetterNode('D', 2)
        val G = LetterNode('G', 2)

        val B = LetterNode('B', 3)
        val C = LetterNode('C', 3)
        val M = LetterNode('M', 3)
        val P = LetterNode('P', 3)

        val F = LetterNode('F', 4)
        val H = LetterNode('H', 4)
        val V = LetterNode('V', 4)
        val W = LetterNode('W', 4)
        val Y = LetterNode('Y', 4)

        val K = LetterNode('K', 5)

        val J = LetterNode('J', 8)
        val X = LetterNode('X', 8)

        val Q = LetterNode('Q', 10)
        val Z = LetterNode('Z', 10)

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

