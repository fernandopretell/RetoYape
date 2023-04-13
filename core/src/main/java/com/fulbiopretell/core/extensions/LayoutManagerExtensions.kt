package com.fulbiopretell.retoyape.core.extensions

import androidx.recyclerview.widget.RecyclerView
import android.view.View

inline fun <reified T : View> androidx.recyclerview.widget.RecyclerView.LayoutManager.findViewInstanceByPosition(position: Int): T? {
    return this.findViewByPosition(position) as? T
}