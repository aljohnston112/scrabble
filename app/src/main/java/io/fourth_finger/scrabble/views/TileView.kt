package io.fourth_finger.scrabble.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.lifecycle.lifecycleScope
import io.fourth_finger.scrabble.ActivityMain
import io.fourth_finger.scrabble.models.Tile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TileView(
    context: Context,
    val tile: Tile?
) : View(context) {

    private val bounds = Rect()

    private val text = tile?.char.toString()
    val pointsText = tile?.points.toString()

    private var textWidth = 0f

    private var letterXOffset = 0f
    private var letterYOffset = 0f

    init {
        (context as ActivityMain).lifecycleScope.launch(Dispatchers.Main.immediate) {
            tile?.invalidate?.collect {
                invalidate()
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (tile != null) {
            val height = bottom - top
            val width = right - left

            val characterPadding = (width * PADDING_RATIO).toInt()
            val paddedWidth = width - characterPadding * 2

            letterTextPaint.textSize = FontSizeUtility.getLargestFontThatFitsSquare(
                tile.char.toString(),
                paddedWidth
            )
            letterTextPaint.getTextBounds(text, 0, text.length, bounds)
            textWidth = pointsTextPaint.measureText(pointsText)

            letterXOffset = (width / 2f)
            letterYOffset = ((height / 2f) - (bounds.exactCenterY()))

            pointsTextPaint.textSize = (paddedWidth / 4f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw border
        setBackgroundColor(Color.parseColor("#c69874"))
        blackBorderPaint.strokeWidth = 8f
        val left = 0f
        val top = 0f
        val right = width.toFloat()
        val bottom = height.toFloat()
        canvas.drawRect(left, top, right, bottom, blackBorderPaint)
        if(tile?.isVerticalRed == true){
            canvas.drawLine(left, top, left, bottom, redBorderPaint)
            canvas.drawLine(right, top, right, bottom, redBorderPaint)
        }
        if(tile?.isHorizontalRed == true){
            canvas.drawLine(left, top, right, top, redBorderPaint)
            canvas.drawLine(left, bottom, right, bottom, redBorderPaint)
        }
        if (tile == null) {
            return
        }

        // Draw the letter
        canvas.drawText(text, letterXOffset, letterYOffset, letterTextPaint)

        // Draw the points
        val pointsXOffset = (width - POINTS_PADDING - textWidth)
        val pointsYOffset = (height - (POINTS_PADDING * 1.5f))
        canvas.drawText(pointsText, pointsXOffset, pointsYOffset, pointsTextPaint)

    }

    companion object {

        private const val POINTS_PADDING = 8
        private const val PADDING_RATIO = 0.25f

        private val blackBorderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        private val redBorderPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }


        private val letterTextPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            isFakeBoldText = false
        }

        private val pointsTextPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
    }

}