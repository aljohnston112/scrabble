package io.fourth_finger.scrabble.models

import android.content.Context
import io.fourth_finger.scrabble.dictionary.DictionaryUtility
import io.fourth_finger.scrabble.dictionary.WordTree

class Dictionary(
    val wordTree: List<WordTree>,
    val definitions: Map<String, String>
){

    companion object {

        var dictionary: Dictionary = Dictionary(
            emptyList(),
            emptyMap()
        )
            private set

        fun loadDictionary(context: Context) {
            dictionary = DictionaryUtility.Companion.getDictionary(context)
        }

    }

}