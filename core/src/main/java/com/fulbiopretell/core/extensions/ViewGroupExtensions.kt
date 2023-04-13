package com.fulbiopretell.retoyape.core.extensions

import com.google.android.material.tabs.TabLayout
import android.view.View
import android.view.ViewGroup

inline fun ViewGroup.forEachIndexed(action: (Int, View) -> Unit) {
    for (index in 0 until childCount) {
        action(index, getChildAt(index))
    }
}

inline fun ViewGroup.forEach(action: (View) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index))
    }
}

inline fun <reified T : View> ViewGroup.forEachInstanceOf(action: (T) -> Unit) {
    for (index in 0 until childCount) {
        (getChildAt(index) as? T)?.run(action)
    }
}

inline fun TabLayout.forEachTab(action: (View) -> Unit) {
    for (index in 0 until tabCount) {
        action((getChildAt(0) as ViewGroup).getChildAt(index))
    }
}

inline fun <reified T : View> ViewGroup.firstOrNull(predicate: (view: T) -> Boolean): T? {
    if (childCount == 0) return null
    else forEachGeneric<T> { if (predicate(it)) return it }
    return null
}

inline fun <reified T : View> ViewGroup.forEachGeneric(action: (T) -> Unit) {
    for (index in 0 until childCount) {
        val childView = getChildAt(index)
        if (childView is T) {
            action(childView)
        }
    }
}

inline fun <reified T : View> ViewGroup.filterIsInstance(): List<T> {
    val list = mutableListOf<T>()
    forEachGeneric<T> { list.add(it) }
    return list
}