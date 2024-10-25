package io.fourth_finger.scrabble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.floor
import kotlin.text.get
import kotlin.text.set

class TileView(
    context: Context,
    val tile: Tile?
) : View(context) {

    private val isDraggingTile = AtomicBoolean(false)

    private val touchListener = object : OnTouchListener {

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            if (view is TileView) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (!isDraggingTile.get()) {
                            val dragShadowBuilder = DragShadowBuilder(view)
                            view.startDragAndDrop(null, dragShadowBuilder, view, 0)
                            view.visibility = INVISIBLE
                            return true
                        } else {
                            return false
                        }
                    }
                    else -> return false
                }
            }
            return false
        }
    }

    private val dragListener = object : OnDragListener {

        override fun onDrag(v: View?, event: DragEvent): Boolean {
            val view = event.localState
            if (view is TileView) {
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        if (!isDraggingTile.get()) {
                            isDraggingTile.set(true)
                            return true
                        } else {
                            return false
                        }
                    }


                    DragEvent.ACTION_DRAG_ENDED -> {
                        isDraggingTile.set(false)
                        return true
                    }

                    else -> return false
                }
            }
            return false
        }
    }

    var isMovable = false
        set(value) {
            field = value
            if (value) {
                setOnTouchListener(touchListener)
                setOnDragListener(dragListener)
            } else {
                setOnTouchListener(null)
                setOnDragListener(null)
            }
        }

    private val borderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }


    private val fontSizeUtility = FontSizeUtility()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw border
        setBackgroundColor(Color.parseColor("#c69874"))
        borderPaint.strokeWidth = if (isMovable) 8f else 4f
        val left = 0f
        val top = 0f
        val right = width.toFloat()
        val bottom = height.toFloat()
        canvas.drawRect(left, top, right, bottom, borderPaint)

        if(tile != null) {
            val paddingRatio = 0.25f
            val characterPadding = (width * paddingRatio).toInt()
            val pointsPadding = 8
            val paddedWidth = width - characterPadding * 2

            // Draw the letter
            textPaint.textSize = fontSizeUtility.getLargestFontThatFitsSquare(
                tile.char.toString(),
                paddedWidth
            ).toFloat()
            val bounds = Rect()
            val text = tile.char.toString()
            textPaint.getTextBounds(text, 0, text.length, bounds)
            val xOffset = (width / 2f)
            val yOffset =
                ((height / 2f) - (bounds.exactCenterY()))

            canvas.drawText(text, xOffset, yOffset, textPaint)

            // Draw the points
            textPaint.textSize = (paddedWidth * 0.25f).toFloat()
            val pointsText = tile.points.toString()
            val pointsXOffset = (width - pointsPadding - textPaint.measureText(pointsText))
            val pointsYOffset = (height - pointsPadding).toFloat()

            canvas.drawText(pointsText, pointsXOffset, pointsYOffset, textPaint)
        }

    }



}