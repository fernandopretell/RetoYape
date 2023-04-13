package com.fulbiopretell.retoyape.core.extensions

import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView


fun RecyclerView.addOnScrollStateChangedListener(f: (newState: Int, RecyclerView.OnScrollListener) -> Unit) {

    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            f(newState, this)
        }
    })

}


fun RecyclerView.addOnScrolledListener(f: (dx: Int, dy: Int, RecyclerView.OnScrollListener) -> Unit) {

    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            f(dx, dy, this)
        }

    })

}

fun RecyclerView.betterSmoothScrollToPosition(targetItem: Int, speed: Float = 0f) {
    layoutManager?.apply {
        val maxScroll = 2
        when (this) {
            is LinearLayoutManager -> {
                val topItem = findFirstVisibleItemPosition()
                val distance = topItem - targetItem
                val anchorItem = when {
                    distance > maxScroll -> targetItem + maxScroll
                    distance < -maxScroll -> targetItem - maxScroll
                    else -> topItem
                }
                if (anchorItem != topItem) scrollToPosition(anchorItem)
                post {
                    when (orientation) {
                        RecyclerView.VERTICAL -> {
                            speedSmoothScrollToPosition(targetItem, speed)
                        }
                        RecyclerView.HORIZONTAL -> {
                            speedHorizontalSmoothScrollToPosition(targetItem, speed)
                        }
                    }
                }
            }
            else -> speedSmoothScrollToPosition(targetItem, speed)
        }
    }
}

fun RecyclerView.speedSmoothScrollToPosition(position: Int, speed: Float) {
    val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return speed / displayMetrics.densityDpi
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }

    if (position != -1) {
        smoothScroller.targetPosition = position
        layoutManager?.apply {
            startSmoothScroll(smoothScroller)
        }
    }
}

fun RecyclerView.speedHorizontalSmoothScrollToPosition(position: Int, speed: Float) {
    val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return speed / displayMetrics.densityDpi
        }

        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_END
        }
    }

    if (position != -1) {
        smoothScroller.targetPosition = position
        layoutManager?.apply {
            startSmoothScroll(smoothScroller)
        }
    }
}

fun RecyclerView.clearDecorations() {
    if (itemDecorationCount > 0) {
        for (i in itemDecorationCount - 1 downTo 0) {
            removeItemDecorationAt(i)
        }
    }
}

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(position: Int, item: T)
}

fun RecyclerView?.getCurrentPosition() : Int {
    return (this?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
}