package com.cgtn.minor.liveminority.utils

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * created by yf on 2019-05-10.
 * recyclerView的距离
 *
 */
class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        if (parent.getChildLayoutPosition(view) != 0)
            outRect.top = space
    }
}