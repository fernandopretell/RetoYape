package com.fulbiopretell.retoyape.core.extensions

fun <T, U, R> Pair<T?, U?>.biLet(body: (T, U) -> R): R? {
    if (first != null && second != null) {
        return body(first as T, second as U)
    }
    return null
}

fun Pair<String, String>.toMap(): HashMap<String, String> {
    return HashMap<String, String>(1).apply {
        put(first, second)
    }
}
