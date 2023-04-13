package com.fulbiopretell.retoyape.core.extensions

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import com.fulbiopretell.retoyape.core.util.SafeClickListener
import kotlin.math.min

private const val animationTimeMills: Long = 150

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun View.dpToPx(dps: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, context.resources.displayMetrics).toInt()

fun View.getDimension(@DimenRes resource: Int) = context.resources.getDimension(resource)

fun View.getDimensionPixelSize(@DimenRes resource: Int) = context.resources.getDimensionPixelSize(resource)

fun View.getString(@StringRes resource: Int, vararg vars: Any) = context.getString(resource, vars)

fun View.getColor(@ColorRes resource: Int) = ContextCompat.getColor(context, resource)

fun View.getColorStateList(@ColorRes resource: Int) = ContextCompat.getColorStateList(context, resource)

fun View.getDrawable(@DrawableRes resource: Int) = ContextCompat.getDrawable(context, resource)

fun View.getFont(@FontRes resource: Int): Typeface? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        resources.getFont(resource)
    } else {
        ResourcesCompat.getFont(context, resource)
    }
}

fun TextView.setTextAppearanceRes(@StyleRes resource: Int) {
    TextViewCompat.setTextAppearance(this, resource)
}


fun View.getColorAttr(@AttrRes attr: Int, @ColorInt default: Int = 0): Int {
    val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return a.getColor(0, default)
    } finally {
        a.recycle()
    }
}

fun View.getDimensionAttr(@AttrRes attr: Int, default: Int = -1): Int {
    val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return a.getDimensionPixelSize(0, default)
    } finally {
        a.recycle()
    }
}

fun View.getBooleanAttr(@AttrRes attr: Int, default: Boolean = false): Boolean {
    val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return a.getBoolean(0, default)
    } finally {
        a.recycle()
    }
}

fun View.getStringAttr(@AttrRes attr: Int): String {
    val v = TypedValue()
    context.theme.resolveAttribute(attr, v, true)
    return v.string as String
}

fun View.getDrawableAttr(@AttrRes attr: Int, fallback: Drawable? = null): Drawable? {
    val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        var d = a.getDrawable(0)
        if (d == null && fallback != null) {
            d = fallback
        }
        return d
    } finally {
        a.recycle()
    }
}


fun View.getCenterX() = width / 2f

fun View.getCenterY() = height / 2f

fun View.getRadius() = getMinSize() / 2f

fun View.getMinSize() = min(width, height)

fun View?.isVisible(): Boolean {
    return this?.visibility == View.VISIBLE
}

/*fun View.fadeIn() {
    this.apply {
        animate()
                .alpha(1f)
                .setDuration(animationTimeMills)
                .setListener(null)
    }
}

fun View.fadeOut() {
    this.apply {
        animate()
                .alpha(0f)
                .setDuration(animationTimeMills)
                .setListener(null)
    }
}*/
