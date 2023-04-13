package com.fulbiopretell.retoyape.core.extensions

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

fun ConstraintLayout.constraints(
    predicate: ConstraintSet.() -> Unit = {}
) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(this)
    predicate.invoke(constraintSet)
    constraintSet.applyTo(this)
}
