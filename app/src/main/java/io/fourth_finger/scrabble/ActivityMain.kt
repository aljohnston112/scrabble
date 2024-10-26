package io.fourth_finger.scrabble

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import io.fourth_finger.scrabble.databinding.ActivityMainBinding
import io.fourth_finger.scrabble.models.Board
import io.fourth_finger.scrabble.models.GameState
import io.fourth_finger.scrabble.models.TileRack
import io.fourth_finger.scrabble.views.GameBoardViewGroup
import io.fourth_finger.scrabble.views.TileRackViewGroup
import io.fourth_finger.scrabble.views.TileView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ActivityMain : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        this.lifecycleScope.launch(Dispatchers.IO){
            GameState.Companion.loadDictionary(this@ActivityMain)
        }

        val gameState = GameState.Companion.getStartingGame()

        // Set up the board
        val minTileSize = 192
        val n = Board.Companion.BOARD_WIDTH_AND_HEIGHT
        val gameBoardView = GameBoardViewGroup(this, n, minTileSize).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(0, 0)
        }
        gameBoardView.minimumHeight = (minTileSize * n)
        gameBoardView.minimumWidth = (minTileSize * n)

        val board = gameState.board
        for(row in 0 until board.board.size) {
            for (col in 0 until board.board[row].size) {
                val tile = board.board[row][col]
                val view = TileView(this, tile).apply {
                    layoutParams = ViewGroup.LayoutParams(minTileSize, minTileSize)
                    id = View.generateViewId()
                }
                gameBoardView.addView(view)
            }
        }

        binding.gameAreaConstraintLayout.addView(gameBoardView)


        // Set up the tile rack
        val tileRackView = TileRackViewGroup(this, TileRack(emptyList())).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(0, minTileSize)
        }
        tileRackView.minimumHeight = (minTileSize)
        tileRackView.addNewTileRack(gameState.tileRack)

        binding.gameAreaConstraintLayout.addView(tileRackView)

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
        constraintSet.connect(tileRackView.id, ConstraintSet.BOTTOM, binding.submitButton.id, ConstraintSet.TOP)
        constraintSet.applyTo(binding.gameAreaConstraintLayout)
        binding.root.requestLayout()


        // Go fullscreen
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        // Show words
        binding.showWords.setOnClickListener{
            val words = mutableListOf<String>()
            if(GameState.Companion.dictionary.isEmpty()){
                Toast.makeText(
                    this,
                    "Dictionary not yet loaded",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                for (tree in GameState.Companion.dictionary) {
                    words.addAll(tree.findWords(gameState.getPlayerChars()))
                }
                if (words.isNotEmpty()) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Possible Words")
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        words
                    )
                    builder.setAdapter(adapter, object : DialogInterface.OnClickListener{
                        override fun onClick(
                            dialog: DialogInterface?,
                            which: Int
                        ) {

                        }
                    })
                    builder.setPositiveButton("Ok", object: DialogInterface.OnClickListener{
                        override fun onClick(
                            dialog: DialogInterface?,
                            which: Int
                        ) {
                           dialog?.dismiss()
                        }
                    })
                    builder.create().show()
                } else {
                    Toast.makeText(
                        this,
                        "No words found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }
}