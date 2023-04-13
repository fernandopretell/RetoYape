package com.fulbiopretell.retoyape.core.extensions

import android.content.res.Resources

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.percent(percent: Int): Int {
    return this * percent / 100
}
