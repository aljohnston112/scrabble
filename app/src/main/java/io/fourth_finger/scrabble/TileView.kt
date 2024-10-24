package io.fourth_finger.scrabble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import java.lang.Integer.min
import kotlin.math.max

class TileView(
    context: Context,
    val tile: Tile
) : View(context) {

    var isMovable = false
        set(value) {
            field = value
            if (value) {
                setOnTouchListener(GameBoardViewGroup.Companion.DragOnDownTouchListener())
            } else {
                setOnTouchListener(null)
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

        val paddingRatio = 0.25f
        val characterPadding = (width * paddingRatio).toInt()
        val pointsPadding = 8

        val paddedWidth = width - characterPadding * 2
        val paddedHeight = height - characterPadding * 2

        // Set the background color
        setBackgroundColor(Color.parseColor("#c69874"))

        // Adjust the border stroke width based on whether the tile is movable
        borderPaint.strokeWidth = if (isMovable) 8f else 4f

        // Draw the border
        val left = 0f
        val top = 0f
        val right = width.toFloat()
        val bottom = height.toFloat()
        canvas.drawRect(left, top, right, bottom, borderPaint)

        // Set text size for the main character
        textPaint.textSize = fontSizeUtility.getLargestFontThatFitsSquare(
            tile.char.toString(),
            paddedWidth
        ).toFloat()

        // Get bounds for the main character text
        val bounds = Rect()
        val text = tile.char.toString()
        textPaint.getTextBounds(text, 0, text.length, bounds)

        // Calculate offsets for centering the main character with padding
        val xOffset = (width / 2f) // Center horizontally
        val yOffset = ((height / 2f) - (bounds.exactCenterY())) // Center vertically, adjust for baseline

        // Draw the main character
        canvas.drawText(text, xOffset, yOffset, textPaint)

        // Set text size for the points (smaller font)
        textPaint.textSize = (paddedWidth * 0.25f).toFloat()

        // Draw the points in the bottom-right corner with 8-pixel padding
        val pointsText = tile.points.toString()
        val pointsXOffset = (width - pointsPadding - textPaint.measureText(pointsText))
        val pointsYOffset = (height - pointsPadding).toFloat()

        canvas.drawText(pointsText, pointsXOffset, pointsYOffset, textPaint)
    }



}