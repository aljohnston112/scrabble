package io.fourth_finger.scrabble.models

import android.content.Context
import io.fourth_finger.scrabble.dictionary.DictionaryUtility
import io.fourth_finger.scrabble.dictionary.WordTree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class Dictionary(
    val wordTrees: List<WordTree>,
    val definitions: Map<String, String>
){

//    suspend fun findWords(
//        chars: List<Char>,
//        board: Board,
//        withDefinitions: Boolean = false
//    ): Collection<String> = withContext(Dispatchers.Default) {
//        val deferredResults = dictionary.wordTrees.mapIndexed { i, wordTree ->
//            async(Dispatchers.Default) {
//                val wordLength = i + 2
//                wordTree.findWords(
//                    chars,
//                    board,
//                    wordLength,
//                    withDefinitions
//                )
//            }
//        }
//        deferredResults.awaitAll().flatten()
//    }


    fun findWords(
        chars: List<Char>,
        board: Board,
        withDefinitions: Boolean = false
    ): Collection<String> {
        val foundWordsWithDefinitions = mutableListOf<String>()

        // 2 letter word tree at index 0, the 3 letter word tree at index 1, etc.
        for((i, wordTree) in dictionary.wordTrees.withIndex()){
            val wordLength = i + 2
            val words = wordTree.findWords(
                chars,
                board,
                wordLength,
                withDefinitions
            )
            foundWordsWithDefinitions.addAll(words)
        }

        return foundWordsWithDefinitions
    }

    companion object {

        var dictionary: Dictionary = Dictionary(
            emptyList(),
            emptyMap()
        )
            private set

        fun loadDictionary(context: Context) {
            dictionary = DictionaryUtility.getDictionary(context)
        }

    }

}