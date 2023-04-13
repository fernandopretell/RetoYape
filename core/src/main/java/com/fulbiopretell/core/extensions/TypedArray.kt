package com.fulbiopretell.retoyape.core.extensions

import android.content.res.TypedArray

fun TypedArray.getResourceIdOrNull(index: Int): Int? {
    return getResourceId(index, -1).takeIf { it != -1 }
}

fun TypedArray.getColorOrNull(index: Int): Int? {
    return getColor(index, -1).takeIf { it != -1 }
}
