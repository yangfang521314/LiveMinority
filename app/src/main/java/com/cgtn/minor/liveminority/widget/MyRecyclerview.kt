package com.cgtn.minor.liveminority.widget

import android.content.Context
import android.support.annotation.Nullable
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent



/**
 * created by yf on 2019-05-14.
 */
class MyRecyclerview :RecyclerView{
    constructor(context: Context): super(context)

    constructor(context: Context, @Nullable attrs: AttributeSet): super(context,attrs)

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyle: Int): super(context,attrs,defStyle)

    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = e.rawX
                mDownY = e.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                //竖向滑动时拦截事件
                val deltaX = e.rawX - mDownX
                val deltaY = e.rawY - mDownY
                if (deltaY.toDouble() != 0.0 && Math.abs(deltaX / deltaY) < 1) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(e)
    }
}