package com.fulbiopretell.retoyape.core.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.PorterDuff
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateMargins
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun View.removeFromParent() {
    this.parent?.let {
        (it as ViewGroup).removeView(this)
    }
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun View.show(show: Boolean) {
    if (show) {
        show()
    } else {
        hide()
    }
}

fun View.showOrDisappear(show: Boolean) {
    if (show) {
        show()
    } else {
        disappear()
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.fadeIn() {
    val currentView = this
    if (!currentView.isVisible) {
        currentView.animate().alpha(1f).setDuration(300)
                .setInterpolator(AccelerateInterpolator()).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        currentView.alpha = 0f
                        visibility = View.VISIBLE
                    }
                }).start()
    }
}

fun View.fadeOut() {
    if (this.isVisible) {
        this.animate().alpha(0f).setDuration(300)
                .setInterpolator(AccelerateInterpolator()).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        visibility = View.GONE
                    }
                }).start()
    }
}

fun View.fadeOutAndExecute(f: () -> Unit) {
    if (this.isVisible) {
        this.animate().alpha(0f).setDuration(300)
                .setInterpolator(AccelerateInterpolator()).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        visibility = View.GONE
                        f()
                    }
                }).start()
    }
}

fun View.fadeInAndExecute(f: () -> Unit) {
    val currentView = this
    if (!currentView.isVisible) {
        currentView.animate().alpha(1f).setDuration(300)
                .setInterpolator(AccelerateInterpolator()).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        currentView.alpha = 0f
                        visibility = View.VISIBLE
                        f()
                    }
                }).start()
    }
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.disappear() {
    this.visibility = View.INVISIBLE
}

fun hideAll(vararg args: View) {
    args.forEach { it.hide() }
}

fun showAll(vararg args: View) {
    args.forEach { it.show() }
}

fun EditText.hideKeyboardEx() = hideKeyboard(context, this)

fun TextView.clear() {
    this.text = ""
}

fun View.tintBackground(@ColorRes colorResource: Int) {
    val color = ContextCompat.getColor(this.context, colorResource)
    this.background?.apply {
        if (isLollipop()) {
            this.mutate().setTint(color)
        } else {
            this.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}

fun View.addTransitionName(text: String) {
    if (isLollipop()) {
        transitionName = text
    }
}

fun TextView.setHtml(text: String) {
    this.text = if (isNougat()) {
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(text)
    }
}

fun View.createSpringAnimation(
        property: DynamicAnimation.ViewProperty,
        finalPosition: Float,
        stiffness: Float,
        dampingRatio: Float): SpringAnimation {
    val animation = SpringAnimation(this, property)
    val spring = SpringForce(finalPosition)
    spring.stiffness = stiffness
    spring.dampingRatio = dampingRatio
    animation.spring = spring
    return animation
}

fun View.getLinearLayoutParams(): LinearLayout.LayoutParams? {
    return tryOrDefault({ this.layoutParams as? LinearLayout.LayoutParams }, null)
}

fun View.setLinearLayoutGravity(gravity: Int) {
    getLinearLayoutParams()?.let { layoutParams ->
        layoutParams.gravity = gravity
        this.layoutParams = layoutParams
    }
}

fun View.updateHorizontalMargins(start: Int, end: Int) {
    (layoutParams as ViewGroup.MarginLayoutParams).updateMargins(
            left = start,
            right = end
    )
}

fun View.updateVerticalMargins(top: Int, bottom: Int) {
    (layoutParams as ViewGroup.MarginLayoutParams).updateMargins(
            top = top,
            bottom = bottom
    )
}

fun RecyclerView.betterSmoothScrollToPosition(targetItem: Int) {
    layoutManager?.apply {
        val maxScroll = 5
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
                    smoothScrollToPosition(targetItem)
                }
            }
            else -> smoothScrollToPosition(targetItem)
        }
    }
}

fun View.setupItemSizeLinearLayout(
        width: Int = LinearLayout.LayoutParams.MATCH_PARENT,
        height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
) {
    if (layoutParams == null) {
        layoutParams = LinearLayout.LayoutParams(width, height)
    } else {
        layoutParams.width = width
        layoutParams.height = height
    }
}