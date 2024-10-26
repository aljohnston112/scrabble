package io.fourth_finger.scrabble.dictionary

import android.content.Context
import io.fourth_finger.scrabble.R
import io.fourth_finger.scrabble.models.Dictionary

class DictionaryUtility {

    companion object {
        
        /**
         * Loads the dictionary and creates a list of trees that represents words and their score.
         * The leaf nodes are the end of the words and contain the core of the entire word without multipliers.
         * The list returned contains the 2 letter word tree at index 0, the 3 letter word tree at index 1, etc.
         */
        fun getDictionary(context: Context): Dictionary {
            val inputStream = context.resources.openRawResource(R.raw.dictionary)
            val trees = mutableListOf<WordTree>()
            val definitions = mutableMapOf<String, String>()

            inputStream.bufferedReader().use { reader ->

                var currentWordLength = 2
                var root = LetterNode.createRoot()
                reader.lineSequence().forEach { line ->
                    val parts = line.split(" ", limit = 2)
                    val word = parts[0]
                    val definition = parts[1]
                    definitions.put(word, definition)

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
            return Dictionary(trees, definitions)
        }

    }

}