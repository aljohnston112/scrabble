package io.fourth_finger.scrabble.dictionary

class LetterFrequencyList(frequencies: MutableMap<Char, Int>) {

    private val _letterFrequencies = mutableMapOf<Char, LetterFrequencyNode>()
    val letterFrequencies: Map<Char, LetterFrequencyNode> = _letterFrequencies

    init {
        for((char, int) in frequencies){
            _letterFrequencies.put(char, LetterFrequencyNode(char, int))
        }
    }

    fun reset(){
        for((_, letterFrequencyNode) in _letterFrequencies){
            letterFrequencyNode.reset()
        }
    }

    class LetterFrequencyNode(
        val char: Char,
        val initialFrequency: Int
    ){
        var frequency = initialFrequency
            private set

        fun decrement(){
            frequency--
        }

        fun reset(){
            frequency = initialFrequency
        }

        fun increment() {
            frequency++
        }


    }

}