package io.fourth_finger.scrabble

import android.os.Bundle
import android.view.View
import android.view.ViewGroup

import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.fourth_finger.scrabble.databinding.ActivityMainBinding


class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Set up the board
        val n = 20
        val minSize = 192
        val gameBoardView = GameBoardViewGroup(this, n, minSize).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(0, 0)
        }
        gameBoardView.minimumHeight = (minSize * n)
        gameBoardView.minimumWidth = (minSize * n)
        (0 until n * n).forEach { i ->
            val view = TileView(this).apply {
                layoutParams = ViewGroup.LayoutParams(minSize, minSize)
            }
            gameBoardView.addView(view)
        }
        binding.gameAreaConstraintLayout.addView(gameBoardView)

        val tileRackView = TileRackViewGroup(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(0, minSize)
        }
        tileRackView.minimumHeight = (minSize)
        (0 until 7).forEach { i ->
            val view = TileView(this).apply {
                layoutParams = ViewGroup.LayoutParams(minSize, minSize)
            }
            view.isMovable = true
            tileRackView.addView(view)
        }
        binding.gameAreaConstraintLayout.addView(tileRackView)

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.gameAreaConstraintLayout)

        // Constraints for gameBoardView
        constraintSet.connect(gameBoardView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(gameBoardView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(gameBoardView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(gameBoardView.id, ConstraintSet.BOTTOM, tileRackView.id, ConstraintSet.TOP)

        // Constraints for tileRackView
        constraintSet.connect(tileRackView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(tileRackView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(tileRackView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

        constraintSet.applyTo(binding.gameAreaConstraintLayout)

        binding.root.requestLayout()

        // Go fullscreen
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    }
}