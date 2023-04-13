@file:JvmName("VersionUtils")

package com.fulbiopretell.retoyape.core.extensions

import android.os.Build

fun isLollipop() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

fun isLollipopOrMinor() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP

fun isMarshmallow() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun isNougat() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

fun isOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

fun isPie() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
