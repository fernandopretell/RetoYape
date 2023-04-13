package com.fulbiopretell.retoyape.core.extensions

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import android.util.TypedValue
import android.view.View


fun View.setBackgroundView(drawable: Drawable?) {
    if (drawable != null) {
        background = drawable
    }
}

fun View.setElevation(elevation: Int) {
    setElevation(elevation, this)
}

fun setElevation(elevation: Int, vararg views: View) {
    if (isLollipop()) {
        views.forEach { view ->
            view.elevation = elevation.toFloat()
        }
    }
}

@SuppressLint("ResourceAsColor")
fun Drawable.setDrawableColor(@ColorRes color: Int) {
    if (isLollipop()) {
        mutate().setTint(color)
    } else {
        setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}

fun View.addRippleForeground() {
    if (isMarshmallow()) {
        val outValue = TypedValue()
        context.theme.resolveAttribute(
                android.R.attr.selectableItemBackgroundBorderless, outValue, true)
        foreground = ContextCompat.getDrawable(context, outValue.resourceId)
    }
}


