package com.fulbiopretell.retoyape.core.extensions

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return this.bitmap
    }
    var width = this.intrinsicWidth
    width = if (width > 0) width else 1
    var height = this.intrinsicHeight
    height = if (height > 0) height else 1
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

fun Drawable.tinted(@ColorInt color: Int): Drawable {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && this is VectorDrawable) {
        this.setColorFilter(BlendModeColorFilter(color, BlendMode.SRC_IN))
        return this
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && this is VectorDrawable) {
        @Suppress("DEPRECATION")
        this.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        return this
    }
    val drawableReturn = DrawableCompat.wrap(this.mutate())
    DrawableCompat.setTintMode(drawableReturn, PorterDuff.Mode.SRC_IN)
    DrawableCompat.setTint(drawableReturn, color)
    return drawableReturn
}
