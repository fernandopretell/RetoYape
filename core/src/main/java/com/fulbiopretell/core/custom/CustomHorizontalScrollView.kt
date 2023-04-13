package com.fulbiopretell.core.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView

class CustomHorizontalScrollView(context: Context, attrs: AttributeSet) : HorizontalScrollView(context, attrs) {

    private lateinit var scrollerTask: Runnable
    private var initialPosition: Int = 0

    private val newCheck = 100

    private var onScrollStoppedListener: OnScrollStoppedListener? = null

    interface OnScrollStoppedListener {
        fun onScrollStopped(position: Int)
    }

    init {
        scrollerTask = Runnable {
            val newPosition = scrollX
            if (initialPosition - newPosition == 0) {//has stopped
                onScrollStoppedListener?.onScrollStopped(newPosition)
            } else {
                initialPosition = scrollX
                this@CustomHorizontalScrollView.postDelayed(scrollerTask, newCheck.toLong())
            }
        }
    }

    fun setOnScrollStoppedListener(listener: CustomHorizontalScrollView.OnScrollStoppedListener) {
        onScrollStoppedListener = listener
    }

    fun startScrollerTask() {
        initialPosition = scrollX
        this@CustomHorizontalScrollView.postDelayed(scrollerTask, newCheck.toLong())
    }

}
