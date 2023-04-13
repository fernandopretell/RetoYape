package com.fulbiopretell.retoyape.core.extensions

inline fun <T> Iterator<T>.iterate(action: (T) -> Unit) {
    while (hasNext()) {
        action.invoke(next())
    }
}

inline fun <T, R> Iterable<T>.intersect(other: Iterable<R>, predicate: (T, R) -> Boolean): Set<T> {
    val set = mutableSetOf<T>()
    for (t1 in this) {
        other.filter { predicate(t1, it) }.forEach { set.add(t1) }
    }
    return set
}

inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapAndAddAll(destination: C, transform: (T) -> Collection<R>): C {
    forEach { item ->
        destination.addAll(transform(item))
    }
    return destination
}

fun <T> Iterable<T>.partition(size: Int): MutableList<MutableList<T>> = with(iterator()) {
    check(size > 0)
    val partitions = mutableListOf<MutableList<T>>()
    while (hasNext()) {
        val partition = mutableListOf<T>()
        do partition.add(next()) while (hasNext() && partition.size < size)
        partitions += partition
    }
    return partitions
}

fun <R, T> Array<out T>.firstResultOrNull(predicate: (T) -> R): R? {
    for (element in this) if (predicate(element) != null) return predicate(element)
    return null
}