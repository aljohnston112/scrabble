package io.fourth_finger.scrabble.dictionary

import android.content.Context
import io.fourth_finger.scrabble.R

class DictionaryUtility {

    companion object {

        const val MAX_WORD_LENGTH = 15

        /**
         * Loads the dictionary and creates a list of trees that represents words and their score.
         * The leaf nodes are the end of the words and contain the core of the entire word without multipliers.
         * The list returned contains the 2 letter word tree at index 0, the 3 letter word tree at index 1, etc.
         */
        fun getDictionary(context: Context): List<WordTree> {
            val inputStream = context.resources.openRawResource(R.raw.dictionary)
            val trees = mutableListOf<WordTree>()

            inputStream.bufferedReader().use { reader ->

                var currentWordLength = 2
                var root = LetterNode.createRoot()
                reader.lineSequence().forEach { line ->
                    val parts = line.split(" ", limit = 2)
                    val word = parts[0]

                    // TODO add the definition
                    val definition = parts[1]

                    // Save the current tree and start the next
                    if (word.length != currentWordLength) {
                        trees.add(root)
                        currentWordLength = word.length
                        root = LetterNode.createRoot()
                    }
                    root.addWord(word)
                }
                trees.add(root)

            }
            return trees
        }

    }

}