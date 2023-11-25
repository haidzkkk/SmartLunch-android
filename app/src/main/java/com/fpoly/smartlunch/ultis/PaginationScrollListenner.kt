package com.fpoly.smartlunch.ultis

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListenner(private val linearLayoutManger: LinearLayoutManager): RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        var visibleItemCount = linearLayoutManger.childCount
        var totalItemCount = linearLayoutManger.itemCount
        var firstVisivleItemPosition = linearLayoutManger.findFirstVisibleItemPosition()

        if (isLoading() || isLastPage()){
            return
        }

        if (firstVisivleItemPosition >= 0 && (visibleItemCount + firstVisivleItemPosition) >= totalItemCount){
            loadMoreItems()
        }

    }

    abstract fun loadMoreItems()
    abstract fun isLoading(): Boolean
    abstract fun isLastPage(): Boolean
}