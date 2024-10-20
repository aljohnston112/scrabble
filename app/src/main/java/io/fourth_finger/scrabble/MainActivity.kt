package io.fourth_finger.scrabble

import android.os.Bundle
import android.view.ViewGroup

import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val n = 20
        val minSize = 192
        val gameBoardView = GameBoardViewGroup(this, n)
        gameBoardView.minimumHeight = (minSize * n)
        gameBoardView.minimumWidth = (minSize * n)
        setContentView(gameBoardView)
        (0 until n * n).forEach { i ->
            val view = TileView(this).apply {
                layoutParams = ViewGroup.LayoutParams(minSize, minSize)
            }
            gameBoardView.addView(view)
        }
    }
}