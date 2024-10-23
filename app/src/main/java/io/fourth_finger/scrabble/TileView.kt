package io.fourth_finger.scrabble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class TileView(context: Context) : View(context) {


    var isMovable = false
        set(value) {
            field = value
            if (value) {
                setOnTouchListener(GameBoardViewGroup.Companion.DragOnDownTouchListener(this))
            } else {
                setOnTouchListener(null)
            }
        }

    private val borderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isMovable) {
            setBackgroundColor(Color.parseColor("red"))
        } else {
            setBackgroundColor(Color.parseColor("#c69874"))
        }

        // Draw the border
        val left = 0f
        val top = 0f
        val right = width.toFloat()
        val bottom = height.toFloat()
        canvas.drawRect(left, top, right, bottom, borderPaint)

    }

}