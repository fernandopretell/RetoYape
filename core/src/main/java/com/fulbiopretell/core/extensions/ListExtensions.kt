package com.fulbiopretell.retoyape.core.extensions

fun <T> List<T>.applyForEach(function: (T) -> Unit): List<T> = apply { forEach(function) }