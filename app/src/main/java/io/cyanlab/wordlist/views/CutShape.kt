package io.cyanlab.wordlist.views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.shapes.Shape

class CutShape : Shape() {

    private val COLOR = Color.WHITE
    private val STROKE_WIDTH = 1f
    private val CORNER = 45.0f

    private val border = Paint()
    private var path: Path = Path()

    init {

        border.color = COLOR
        border.style = Paint.Style.FILL
        border.strokeWidth = STROKE_WIDTH
        border.isAntiAlias = true
        //border.isDither = true
        //border.strokeJoin = Paint.Join.ROUND
        //border.strokeCap = Paint.Cap.ROUND
    }

    override fun onResize(width: Float, height: Float) {
        super.onResize(width, height)

        val dx = STROKE_WIDTH / 2.0f
        val dy = STROKE_WIDTH / 2.0f
        val w = width - dx
        val h = height - dy

        path.reset()
        //Top
        path.moveTo(dx + CORNER, dy)
        path.lineTo(w, dy)

        path.lineTo(w, h)
        path.lineTo(dx, h)

        path.lineTo(dx, h - CORNER)
        path.lineTo(dx, dy + CORNER)
        path.close()
    }

    override fun draw(p0: Canvas?, p1: Paint?) {
        p0!!.drawPath(path, border)
    }
}