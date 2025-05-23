package io.fourth_finger.scrabble.dictionary

import io.fourth_finger.scrabble.models.Board
import io.fourth_finger.scrabble.models.Dictionary
import io.fourth_finger.scrabble.models.Tile
import kotlin.collections.ArrayDeque
import kotlin.text.forEach

class WordTree(private val value: LetterNode) {

    private val _children: MutableList<WordTree> = mutableListOf()
    private val children: List<WordTree> = _children

    private fun addChild(child: WordTree) {
        _children.add(child)
    }

    fun addWord(word: String) {
        var currentNode = this

        word.uppercase().forEach { char ->
            val temporaryLetterNode = LetterNode.letterNodeMap[char]!!

            val existingNode = currentNode.children.find {
                it.value.char == temporaryLetterNode.char
            }
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
     * Finds all words that can be made with the given characters.
     * This node does not count as the first letter.
     * The children nodes are the first characters of the words.
     */
    private fun findWords(
        chars: List<Char>,
        useDictionary: Boolean = false,
        indexToLetter: Map<Int, Char> = emptyMap()
    ): MutableList<String> {
        val foundWords = mutableListOf<String>()
        val charFrequencies = LetterFrequencyList(getCharFrequency(chars))

        val queue = ArrayDeque<Pair<WordTree, Boolean>>()
        for (i in children.lastIndex downTo 0) {
            queue.addLast(Pair(children[i], false))
        }

        var word = ""
        while (queue.isNotEmpty()) {
            val childPair = queue.removeLast()
            val child = childPair.first
            val visited = childPair.second
            val childChar = child.value.char

            if (visited) {
                charFrequencies.letterFrequencies[childChar]!!.increment()
                word = word.substring(0, word.length - 1)
            } else {
                var decremented = false
                val hasLetter = charFrequencies.letterFrequencies.getOrDefault(
                    childChar,
                    LetterFrequencyList.LetterFrequencyNode(childChar, 0)
                ).frequency > 0

                val nextLetterIndex = word.length + 1
                val needLetter = !indexToLetter.containsKey(nextLetterIndex)
                val hasNeededLetter = (needLetter && hasLetter)
                val letterOnBoardMatches = indexToLetter[nextLetterIndex] == childChar

                // If hasNeededLetter, the space on the board is empty
                if (hasNeededLetter || letterOnBoardMatches) {
                    word += childChar
                    charFrequencies.letterFrequencies[childChar]!!.decrement()
                    decremented = true
                    if (child.children.isEmpty()) {
                        foundWords.add(word)
                        if (useDictionary) {
                            foundWords.add(Dictionary.dictionary.definitions[word]!!)
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
                    for (i in child.children.lastIndex downTo 0) {
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

    fun findWords(
        chars: List<Char>,
        board: Board,
        depth: Int,
        includeDefinitions: Boolean = false
    ): Set<String> {
        val foundWords = mutableSetOf<String>()

        if(!board.hasWords()){
            foundWords.addAll(
                findWords(
                    chars,
                    includeDefinitions
                )
            )
        }

        // Check each row
        for (row in 0 until board.board.size - depth + 1) {
            foundWords.addAll(
                findWords(
                    chars,
                    board,
                    board.board[row],
                    row,
                    true,
                    includeDefinitions
                )
            )
        }

        // Check each column
        for (col in 0 until board.board[0].size) {
            val colList = mutableListOf<Tile?>()
            for (row in 0 until board.board.size- depth + 1) {
                colList.add(board.board[row][col])
            }
            foundWords.addAll(
                findWords(
                    chars,
                    board,
                    colList,
                    col,
                    false,
                    includeDefinitions
                )
            )
        }
        return foundWords
    }

    fun findWords(
        chars: List<Char>,
        board: Board,
        rowOrColumn: List<Tile?>,
        rowOrColNumber: Int,
        isRow: Boolean,
        includeDefinitions: Boolean = false
    ): List<String> {
        val foundWords = mutableListOf<String>()
        val charFrequencies = LetterFrequencyList(getCharFrequency(chars))

        val indexToLetter = mutableMapOf<Int, Char>()
        for (i in rowOrColumn.indices) {
            val tile = rowOrColumn[i]
            if (tile != null) {
                indexToLetter[i] = tile.char
            }
        }

        for (wordStartIndex in rowOrColumn.indices) {
            val queue = ArrayDeque<Pair<WordTree, Boolean>>()
            for (j in children.lastIndex downTo 0) {
                queue.addLast(Pair(children[j], false))
            }

            var word = ""
            while (queue.isNotEmpty()) {
                val childPair = queue.removeLast()
                val child = childPair.first
                val visited = childPair.second
                val childChar = child.value.char

                if (visited) {
                    if(!indexToLetter.containsKey(word.length - 1)) {
                        charFrequencies.letterFrequencies[childChar]!!.increment()
                    }
                    word = word.substring(0, word.length - 1)
                } else {
                    var decremented = false
                    val hasLetter = charFrequencies.letterFrequencies.getOrDefault(
                        childChar,
                        LetterFrequencyList.LetterFrequencyNode(childChar, 0)
                    ).frequency > 0

                    val nextLetterIndex = word.length
                    val needLetter = !indexToLetter.containsKey(nextLetterIndex)
                    val hasNeededLetter = (needLetter && hasLetter)
                    val letterOnBoardMatches = indexToLetter[nextLetterIndex] == childChar

                    // If hasNeededLetter, the space on the board is empty
                    if (hasNeededLetter || letterOnBoardMatches) {
                        word += childChar

                        if(!letterOnBoardMatches) {
                            charFrequencies.letterFrequencies[childChar]!!.decrement()
                            decremented = true
                        }

                        if (child.children.isEmpty()) {

                            val isPlayValid = isPlayValid(
                                word,
                                isRow,
                                rowOrColNumber,
                                wordStartIndex,
                                board
                            )

                            if (isPlayValid) {
                                foundWords.add(word)
                                if (includeDefinitions) {
                                    foundWords.add(Dictionary.dictionary.definitions[word]!!)
                                }
                            }
                        }
                    }

                    if (decremented || letterOnBoardMatches) {
                        if (child.children.isEmpty()) {
                            if (!letterOnBoardMatches) {
                                charFrequencies.letterFrequencies[childChar]!!.increment()
                            }
                            word = word.substring(0, word.length - 1)
                        } else {
                            queue.addLast(Pair(child, true))
                        }
                        for (i in child.children.lastIndex downTo 0) {
                            queue.addLast(Pair(child.children[i], false))
                        }
                    }

                }

            }
            charFrequencies.reset()
        }
        return foundWords
    }

    fun isPlayValid(
        word: String,
        isWordHorizontal: Boolean,
        rowOrColNumberOfWord: Int,
        wordStartIndex: Int,
        board: Board,
    ): Boolean {
        return if (isWordHorizontal) {
            checkThatHorizontalPlayIsValid(
                word,
                wordStartIndex,
                rowOrColNumberOfWord,
                board,
            )
        } else {
            checkThatVerticalPlayIsValid(
                word,
                wordStartIndex,
                rowOrColNumberOfWord,
                board,
            )
        }
    }

    private fun checkThatHorizontalPlayIsValid(
        word: String,
        wordStartIndex: Int,
        rowNumberOfWord: Int,
        board: Board
    ): Boolean {
        var isValidPlay = true

        for (currentColumnInWord in word.indices) {
            val currentColumn = wordStartIndex + currentColumnInWord

            if(currentColumn < board.board[0].size) {
                // Check the tiles above
                var hasTileAbove = true
                var currentRowIndex = rowNumberOfWord - 1
                var currentTile: Tile? = null
                if (currentRowIndex > 0) {
                    currentTile = board.board[currentRowIndex][currentColumn]
                    while (currentRowIndex > 0 && currentTile != null) {
                        currentRowIndex--
                        currentTile = board.board[currentRowIndex][currentColumn]
                    }
                    if (currentTile == null && currentRowIndex == rowNumberOfWord - 1) {
                        hasTileAbove = false
                    } else {
                        currentRowIndex++
                        currentTile = board.board[currentRowIndex][currentColumn]
                    }
                }

                // Add the letters above to the word
                var verticalWord = ""
                if (hasTileAbove) {
                    while (currentRowIndex < rowNumberOfWord - 1) {
                        verticalWord += currentTile!!.char
                        currentRowIndex++
                        currentTile = board.board[currentRowIndex][currentColumn]
                    }
                }
                verticalWord += word[currentColumnInWord]

                // Add the letters below to the word
                currentRowIndex = rowNumberOfWord + 1
                if (currentRowIndex < board.board.size) {
                    currentTile = board.board[currentRowIndex][currentColumn]
                    while (currentRowIndex < board.board.size - 1 && currentTile != null) {
                        verticalWord += currentTile.char
                        currentRowIndex++
                        currentTile = board.board[currentRowIndex][currentColumn]
                    }
                }
                if (verticalWord.length != 1) {
                    isValidPlay = Dictionary.dictionary.definitions.containsKey(verticalWord)
                }
                if (!isValidPlay) {
                    break
                }
            }
        }

        return isValidPlay
    }

    private fun checkThatVerticalPlayIsValid(
        word: String,
        wordStartIndex: Int,
        colNumberOfWord: Int,
        board: Board
    ): Boolean {
        var isValidPlay = true

        for (currentRowInWord in word.indices) {
            val currentRow = wordStartIndex + currentRowInWord

            if(currentRow < board.board.size) {
                // Check the tiles to the left
                var hasTileOnLeft = true
                var currentColIndex = colNumberOfWord - 1
                var currentTile: Tile? = null
                if (currentColIndex > 0) {
                    currentTile = board.board[currentRow][currentColIndex]
                    while (currentColIndex > 0 && currentTile != null) {
                        currentColIndex--
                        currentTile = board.board[currentRow][currentColIndex]
                    }
                    if (currentTile == null && currentColIndex == colNumberOfWord - 1) {
                        hasTileOnLeft = false
                    } else {
                        currentColIndex++
                        currentTile = board.board[currentRow][currentColIndex]
                    }
                }

                // Add the letters on the left to the word
                var horizontalWord = ""
                if (hasTileOnLeft) {
                    while (currentColIndex < colNumberOfWord - 1) {
                        horizontalWord += currentTile!!.char
                        currentColIndex++
                        currentTile = board.board[currentRow][currentColIndex]
                    }
                }
                horizontalWord += word[currentRowInWord]

                // Add the letters on the right of the word
                currentColIndex = colNumberOfWord + 1
                if (currentColIndex < board.board.size) {
                    currentTile = board.board[currentRow][currentColIndex]
                    while (currentColIndex < board.board.size - 1 && currentTile != null) {
                        horizontalWord += currentTile.char
                        currentColIndex++
                        currentTile = board.board[currentRow][currentColIndex]
                    }
                }

                if (horizontalWord.length != 1) {
                    isValidPlay = Dictionary.dictionary.definitions.containsKey(horizontalWord)
                }
                if (!isValidPlay) {
                    break
                }
            }
        }

        return isValidPlay
    }

}

