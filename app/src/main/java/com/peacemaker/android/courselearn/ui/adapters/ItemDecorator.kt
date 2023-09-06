package com.peacemaker.android.courselearn.ui.adapters

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View


class RecyclerViewItemDecoration(private val spanCount: Int, private val spacing: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = spacing - column * spacing / spanCount
        outRect.right = (column + 1) * spacing / spanCount

        if (position >= spanCount) {
            outRect.top = spacing
        }
    }
}



class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // Calculate both horizontal and vertical spacing equally
        val spacingPx = spacing / 2

        outRect.left = spacingPx
        outRect.right = spacingPx

        if (parent.getChildAdapterPosition(view) < spanCount) {
            outRect.top = spacingPx
        }
        outRect.bottom = spacingPx
    }
}



