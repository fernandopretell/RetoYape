package com.fulbiopretell.retoyape.core.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.fulbiopretell.retoyape.core.GlideApp

fun ImageView.setSvgColor(color: Int) =
    this.setColorFilter(color, PorterDuff.Mode.SRC_IN)

fun ImageView.setSvgColor(context: Context, color: Int) =
    this.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)

fun ImageView.applyColorFilterSoldOut() {
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    val filter = ColorMatrixColorFilter(colorMatrix)
    this.colorFilter = filter
}

fun ImageView.loadUrl(url: String?, placeholder: Drawable? = null, error: Drawable? = null) {
    val ph = placeholder ?: ColorDrawable(Color.WHITE)
    val er = error ?: ph
    if (url.isNullOrEmpty()) {
        GlideApp.with(context)
            .load(ph)
            .into(this)
    } else {
        val requestOptions = RequestOptions()
            .placeholder(ph)
            .error(er)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        GlideApp.with(context)
            .load(url)
            .apply(requestOptions)
            .into(this)
    }
}
