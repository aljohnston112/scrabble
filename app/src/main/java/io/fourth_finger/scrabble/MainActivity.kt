package io.fourth_finger.scrabble

import android.os.Bundle
import android.view.View
import android.view.ViewGroup

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.fourth_finger.scrabble.databinding.ActivityMainBinding


class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    val viewModel: ActivityMainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Set up the board
        val minTileSize = 192
        val n = Board.BOARD_WIDTH_AND_HEIGHT
        val gameBoardView = GameBoardViewGroup(this, n, minTileSize).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(0, 0)
        }
        gameBoardView.minimumHeight = (minTileSize * n)
        gameBoardView.minimumWidth = (minTileSize * n)
        binding.gameAreaConstraintLayout.addView(gameBoardView)

        viewModel.board.observe(this){ board ->
            gameBoardView.removeAllViews()
            for(row in 0 until board.board.size) {
                for (col in 0 until board.board[row].size) {
                    val tile = board.board[row][col]
                    val view = TileView(this, tile).apply {
                        layoutParams = ViewGroup.LayoutParams(minTileSize, minTileSize)
                    }
                    gameBoardView.addView(view)
                }
            }
        }

        // Set up the tile rack
        val tileRackView = TileRackViewGroup(this, TileRack(emptyList())).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(0, minTileSize)
        }
        tileRackView.minimumHeight = (minTileSize)
        binding.gameAreaConstraintLayout.addView(tileRackView)

        viewModel.tileRack.observe(this) { tileRack ->
            tileRackView.addNewTileRack(tileRack)
        }

        // Set up constraints
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