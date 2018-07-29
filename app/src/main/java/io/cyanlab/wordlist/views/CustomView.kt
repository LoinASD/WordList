package io.cyanlab.wordlist.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class CustomView(private val mContext: Context, attrs: AttributeSet) : View(mContext, attrs) {


/*    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {


        super.onLayout()
    }*/

    internal var paint: Paint
    internal lateinit var path: Path

    init {
        setWillNotDraw(false)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    protected override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        paint.strokeWidth = 2F
        paint.color = Color.rgb(0, 0x97, 0xa7)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true

        path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        path.moveTo(0f, h/3)
        path.lineTo(0f, h)
        path.lineTo(w - 125, h)
        path.close()
        canvas.drawPath(path, paint)
    }
}