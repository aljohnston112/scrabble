package io.fourth_finger.scrabble

import android.app.AlertDialog
import android.content.res.AssetManager
import android.os.Bundle
import android.view.DragEvent
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
import io.fourth_finger.scrabble.models.Dictionary
import io.fourth_finger.scrabble.models.TileRack
import io.fourth_finger.scrabble.views.DragMonitor
import io.fourth_finger.scrabble.views.GameBoardViewGroup
import io.fourth_finger.scrabble.views.TileRackViewGroup
import io.fourth_finger.scrabble.views.TileView
import io.fourth_finger.scrabble.views.ViewUtil.Companion.removeFromParent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.ByteBuffer


class ActivityMain : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private val gameStateContainer = GameStateContainer()
    private val dragMonitor = DragMonitor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        System.loadLibrary("native-lib");

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        loadDictionary()

        goFullScreen()

        setUpConstraints(
            setUpGameBoardViewGroup(),
            setUpTileRackViewGroup()
        )

        setUpDragListenerOnRoot()
        setUpSubmitButton()
        setUpShowWordsButton()

    }

    private fun loadDictionary() {
        this.lifecycleScope.launch(Dispatchers.IO) {
            Dictionary.loadDictionary(this@ActivityMain)
        }
    }

    private fun goFullScreen() {
        val windowInsetsController = WindowCompat.getInsetsController(
            window,
            window.decorView
        )
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun setUpGameBoardViewGroup(): GameBoardViewGroup {
        return GameBoardViewGroup(
            this,
            gameStateContainer,
            dragMonitor,
            MIN_TILE_SIZE
        ).apply {
            id = View.generateViewId()
            // Needs to be 0 otherwise it may take up more space than it needs to
            layoutParams = ConstraintLayout.LayoutParams(
                0,
                0
            )

            // Populate with tile views
            val board = gameStateContainer.gameState.gameBoard.board
            for (row in board.indices) {
                for (col in board[row].indices) {
                    val view = TileView(
                        this@ActivityMain,
                        board[row][col]
                    ).apply {
                        id = View.generateViewId()
                    }
                    this.addView(view)
                }
            }

            binding.gameAreaConstraintLayout.addView(this)
        }
    }

    private fun setUpTileRackViewGroup(): TileRackViewGroup {
        return TileRackViewGroup(
            this,
            TileRack(emptyList()),
            dragMonitor
        ).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                0,
                MIN_TILE_SIZE
            )
            addNewTileRack(gameStateContainer.gameState.tileRack)
            binding.gameAreaConstraintLayout.addView(this)
        }
    }

    private fun setUpConstraints(
        gameBoardView: GameBoardViewGroup,
        tileRackView: TileRackViewGroup
    ) {
        ConstraintSet().apply {
            clone(binding.gameAreaConstraintLayout)

            // Constraints for gameBoardView
            connect(
                gameBoardView.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            connect(
                gameBoardView.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )
            connect(
                gameBoardView.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            connect(
                gameBoardView.id,
                ConstraintSet.BOTTOM,
                tileRackView.id,
                ConstraintSet.TOP
            )

            // Constraints for tileRackView
            connect(
                tileRackView.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )
            connect(
                tileRackView.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            connect(
                tileRackView.id,
                ConstraintSet.BOTTOM,
                binding.submitButton.id,
                ConstraintSet.TOP
            )
            applyTo(binding.gameAreaConstraintLayout)
        }
        binding.root.requestLayout()
    }

    private fun setUpDragListenerOnRoot() {
        binding.gameAreaConstraintLayout.setOnDragListener(object : View.OnDragListener {
            override fun onDrag(
                v: View,
                event: DragEvent
            ): Boolean {
                val view = event.localState
                if(view is TileView) {
                    when (event.action) {
                        DragEvent.ACTION_DRAG_STARTED -> {
                                dragMonitor.setDragInProgress(true)
                                dragMonitor.setInitialParent(view.parent as ViewGroup)
                            return true
                        }

                        DragEvent.ACTION_DROP -> {
                            view.removeFromParent()
                            return true
                        }

                        DragEvent.ACTION_DRAG_ENDED -> {
                                dragMonitor.setDragInProgress(false)
                            return false
                        }

                    }
                }
                return false
            }
        })
    }

    private fun setUpShowWordsButton() {
        binding.showWords.setOnClickListener {
            val wordTrees = Dictionary.dictionary.wordTrees
            if (wordTrees.isEmpty()) {
                Toast.makeText(
                    this,
                    "Dictionary not yet loaded",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Look for all words that can be made
            val wordsWithDefinitions = mutableListOf<String>()
            val gameState = gameStateContainer.gameState
            runBlocking {
                wordsWithDefinitions.addAll(
                    Dictionary.dictionary.findWords(
                        gameState.getPlayerChars(),
                        gameState.gameBoard,
                        true
                    )
                )
            }

            // Create a popup to show all words with their definitions
            if (wordsWithDefinitions.isEmpty()) {
                Toast.makeText(
                    this,
                    "No words found",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Possible Words")
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                wordsWithDefinitions
            )
            builder.setAdapter(adapter) { _, _ ->

            }
            builder.setPositiveButton("Ok") { dialog, _ ->
                dialog?.dismiss()
            }
            builder.create().show()
        }
    }

    private fun setUpSubmitButton() {
        var boardChanged = false
        lifecycleScope.launch(Dispatchers.IO) {
            gameStateContainer.gameStateChanged.collect {
                boardChanged = true
            }
        }

        var currentTurnGameState = gameStateContainer.gameState
        binding.submitButton.setOnClickListener {

            if (boardChanged) {
                Board.checkUserPlay(
                    currentTurnGameState.gameBoard,
                    gameStateContainer.gameState.gameBoard
                )
                currentTurnGameState = gameStateContainer.gameState
            }

            //            if(boardChanged){
            //                Dictionary.dictionary.wordTree.checkThatPlayIsValid(
            //                    word,
            //                    isRow,
            //                    rowOrColNumber,
            //                    wordStartIndex,
            //                    board,
            //                    Dictionary.dictionary.wordTree
            //                )
            //            }
            boardChanged = false
        }
    }


    companion object {

        private const val MIN_TILE_SIZE = 192
    }

}

