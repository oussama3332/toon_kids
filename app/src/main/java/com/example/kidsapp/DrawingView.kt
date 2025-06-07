package com.example.kidsapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var path = Path()
    private val paint = Paint()
    private val paths = ArrayList<Pair<Path, Paint>>()
    private val undonePaths = ArrayList<Pair<Path, Paint>>()

    private var currentColor = Color.BLACK
    private var strokeWidth = 10f

    init {
        paint.color = currentColor
        paint.isAntiAlias = true
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((p, paint) in paths) {
            canvas.drawPath(p, paint)
        }
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                val newPaint = Paint(paint)
                paths.add(Pair(Path(path), newPaint))
                path.reset()
                undonePaths.clear()
            }
        }
        invalidate()
        return true
    }

    fun setColor(color: Int) {
        currentColor = color
        paint.color = color
    }

    fun setStrokeWidth(width: Float) {
        strokeWidth = width
        paint.strokeWidth = width
    }

    fun undo() {
        if (paths.isNotEmpty()) {
            undonePaths.add(paths.removeAt(paths.size - 1))
            invalidate()
        }
    }

    fun redo() {
        if (undonePaths.isNotEmpty()) {
            paths.add(undonePaths.removeAt(undonePaths.size - 1))
            invalidate()
        }
    }

    fun clear() {
        paths.clear()
        undonePaths.clear()
        path.reset()
        invalidate()
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}
