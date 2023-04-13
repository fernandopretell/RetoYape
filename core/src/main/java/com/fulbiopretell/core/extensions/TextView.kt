package com.fulbiopretell.retoyape.core.extensions

import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat


fun TextView.setFont(@FontRes value: Int?) {
    typeface = value?.let { ResourcesCompat.getFont(context, it) }
}

fun TextView.hideIfNullOrEmpry() = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE


fun TextView.typeValuePX(size: Float) = setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
