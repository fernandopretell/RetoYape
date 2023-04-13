package com.fulbiopretell.retoyape.core.extensions

import androidx.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import android.widget.TextView

@BindingAdapter(value = ["drawable_right", "drawable_color"], requireAll = false)
fun TextView.setDrawableRight(drawableRes: Drawable?, @ColorInt colorRes: Int) {
    val drawables = this.compoundDrawables
    if (colorRes == 0) {
        this.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawableRes, drawables[3])
    } else {
        drawableRes?.let {
            val wrappedDrawable = DrawableCompat.wrap(it).mutate()
            DrawableCompat.setTint(wrappedDrawable, colorRes)
            this.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], wrappedDrawable, drawables[3])
        }
    }
}

@BindingAdapter(value = ["drawable_left", "drawable_color"], requireAll = false)
fun TextView.setDrawableLeft(drawableRes: Drawable?, @ColorInt colorRes: Int) {
    val drawables = this.compoundDrawables
    if (colorRes == 0) {
        this.setCompoundDrawablesWithIntrinsicBounds(drawableRes, drawables[1], drawables[2], drawables[3])
    } else {
        drawableRes?.let {
            val wrappedDrawable = DrawableCompat.wrap(it).mutate()
            DrawableCompat.setTint(wrappedDrawable, colorRes)
            this.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, drawables[1], drawables[2], drawables[3])
        }
    }
}

@BindingAdapter(value = ["drawable_top", "drawable_color"], requireAll = false)
fun TextView.setDrawableTop(drawableRes: Drawable?, @ColorInt colorRes: Int) {
    val drawables = this.compoundDrawables
    drawableRes?.let {
        val wrappedDrawable = DrawableCompat.wrap(it).mutate()
        if (colorRes > 0) DrawableCompat.setTint(wrappedDrawable, colorRes)
        this.setCompoundDrawablesWithIntrinsicBounds(drawables[0], wrappedDrawable, drawables[2], drawables[3])
    }
}