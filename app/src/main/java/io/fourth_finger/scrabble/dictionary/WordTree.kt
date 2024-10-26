package io.fourth_finger.scrabble.dictionary

import kotlin.collections.ArrayDeque
import kotlin.text.forEach

class WordTree(val value: LetterNode) {

    private val _children: MutableList<WordTree> = mutableListOf()
    private val children: List<WordTree> = _children

    fun addChild(child: WordTree) {
        _children.add(child)
    }

    fun addWord(word: String) {
        var currentNode = this

        word.uppercase().forEach { char ->
            val temporaryLetterNode = LetterNode.letterNodeMap[char]!!

            val existingNode =
                currentNode.children.find { it.value.char == temporaryLetterNode.char }
            if (existingNode != null) {
                currentNode = existingNode
            } else {
                val letterNode = LetterNode.letterNodeMap[temporaryLetterNode.char]!!
                val newNode = WordTree(letterNode)
                currentNode.addChild(newNode)
                currentNode = newNode
            }
        }
    }

    /**
     * Finds all words that can be made with the given character.
     * This node does not count as the first letter.
     * The children nodes are the first characters of the words.
     */
    fun findWords(
        chars: List<Char>,
        dictionary: Map<String, String>? = null
    ): MutableList<String> {

        val hasDictionary = dictionary != null
        val foundWords = mutableListOf<String>()
        val charFrequencies = LetterFrequencyList(getCharFrequency(chars))

        var queue = ArrayDeque<Pair<WordTree, Boolean>>()
        for(i in children.lastIndex downTo 0){
            queue.addLast(Pair(children[i], false))
        }

        var word = ""
        while (queue.isNotEmpty()) {
            val childPair = queue.removeLast()
            val child = childPair.first
            val visited = childPair.second
            val childChar = child.value.char

            if(visited){
                charFrequencies.letterFrequencies[childChar]!!.increment()
                word = word.substring(0, word.length - 1)
            } else {
                var decremented = false
                if (
                    charFrequencies.letterFrequencies.getOrDefault(
                        childChar,
                        LetterFrequencyList.LetterFrequencyNode(childChar, 0)
                    ).frequency > 0
                ) {
                    word += childChar
                    charFrequencies.letterFrequencies[childChar]!!.decrement()
                    decremented = true
                    if (child.children.isEmpty()) {
                        foundWords.add(word)
                        if(hasDictionary){
                            foundWords.add(dictionary[word]!!)
                        }
                    }
                }

                if (decremented) {
                    if (child.children.isEmpty()) {
                        charFrequencies.letterFrequencies[childChar]!!.increment()
                        word = word.substring(0, word.length - 1)
                    } else {
                        queue.addLast(Pair(child, true))
                    }
                    for(i in child.children.lastIndex downTo 0){
                        queue.addLast(Pair(child.children[i], false))
                    }
                }

            }

        }
        return foundWords
    }

    private fun getCharFrequency(chars: List<Char>): MutableMap<Char, Int> {
        val charFrequency = mutableMapOf<Char, Int>()
        chars.forEach { char ->
            charFrequency[char] = charFrequency.getOrDefault(char, 0) + 1
        }
        return charFrequency
    }

}