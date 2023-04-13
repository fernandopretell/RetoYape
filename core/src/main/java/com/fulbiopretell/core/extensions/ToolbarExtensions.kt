package com.fulbiopretell.retoyape.core.extensions

import androidx.databinding.BindingAdapter
import android.graphics.PorterDuff
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.Toolbar

@BindingAdapter(value = ["back_icon", "icon_color"], requireAll = false)
fun Toolbar.setNavigationIcon(@DrawableRes iconResource: Int = 0, @ColorRes iconColor: Int = 0) {
    if (iconResource != 0) {
        val icon = ContextCompat.getDrawable(context, iconResource)
        if (iconColor != 0 && icon != null) {
            icon.apply {
                mutate()
                setColorFilter(ContextCompat.getColor(context, iconColor), PorterDuff.Mode.MULTIPLY)
            }
        }
        navigationIcon = icon
    }
}
