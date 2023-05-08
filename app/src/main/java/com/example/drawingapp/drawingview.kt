package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View


class drawingview(context: Context, attrs: AttributeSet) :View(context,attrs){

    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawpaint: Paint? = null
    private var mcanvasPaint: Paint? =null
    private var mBrushsize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private var mPath = ArrayList<CustomPath>()
    private var mUndoPath = ArrayList<CustomPath>()
    private var mRedoPath = ArrayList<CustomPath>()

    init {
        setupDrawing()
    }

    fun onClickUndo() {
        if (mPath.size > 0) {
            mUndoPath.add(mPath.removeAt(mPath.size-1))
            invalidate()
        }
    }

    fun onClickRedo() {
        if (mUndoPath.size > 0) {
            mPath.add(mUndoPath.removeAt(mUndoPath.size-1))
            invalidate()
        }
    }

    private fun setupDrawing()
    {
        mDrawpaint= Paint()
        mDrawPath= CustomPath(color,mBrushsize)
        mDrawpaint!!.color = color
        mDrawpaint!!.style = Paint.Style.STROKE
        mDrawpaint!!.strokeJoin = Paint.Join.ROUND
        mDrawpaint!!.strokeCap = Paint.Cap.ROUND
        mcanvasPaint = Paint(Paint.DITHER_FLAG)
//        mBrushsize = 20.toFloat()

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mcanvasPaint)

        for (path in mPath)
        {
            mDrawpaint!!.strokeWidth = path.brushThickness
            mDrawpaint!!.color = path.color
            canvas.drawPath(path,mDrawpaint!!)
        }

        if (!mDrawPath!!.isEmpty)
        {
            mDrawpaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawpaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!,mDrawpaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val touchX = event?.x
        val touchY = event?.y

        when(event?.action)
        {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushsize

                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE-> {
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                mPath.add(mDrawPath!!)
                mDrawPath = CustomPath(color,mBrushsize)
            }
            else -> return false
        }
        invalidate()

        return true
    }

    fun setBrushsize(newSize: Float){
        mBrushsize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            newSize,resources.displayMetrics
            )
        mDrawpaint!!.strokeWidth = mBrushsize
    }

    fun setColor(newColor: String)
    {
        color = Color.parseColor(newColor)
        mDrawpaint!!.color = color

    }

    internal inner class CustomPath( var color:Int,
                                     var brushThickness:Float): Path() {

    }
}