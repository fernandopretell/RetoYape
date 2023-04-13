package com.fulbiopretell.core.custom

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class CustomVerticalNestedScrollView(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs) {

    private lateinit var scrollerTask: Runnable
    private var initialPosition: Int = 0

    private val newCheck = 100

    private var onScrollStoppedListener: OnScrollStoppedListener? = null

    interface OnScrollStoppedListener {
        fun onScrollStopped(position: Int)
    }

    init {
        scrollerTask = Runnable {
            val newPosition = scrollY
            if (initialPosition - newPosition == 0) {//has stopped
                onScrollStoppedListener?.onScrollStopped(newPosition)
            } else {
                initialPosition = scrollY
                this@CustomVerticalNestedScrollView.postDelayed(scrollerTask, newCheck.toLong())
            }
        }
    }

    fun setOnScrollStoppedListener(listener: CustomVerticalNestedScrollView.OnScrollStoppedListener) {
        onScrollStoppedListener = listener
    }

    fun startScrollerTask() {
        initialPosition = scrollY
        this@CustomVerticalNestedScrollView.postDelayed(scrollerTask, newCheck.toLong())
    }

}
