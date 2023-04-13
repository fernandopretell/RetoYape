package com.fulbiopretell.retoyape.core.extensions

import java.util.ArrayList
import java.util.LinkedHashMap

class IndexedHashMap<K, V> : LinkedHashMap<K, V>() {

    private var arrayKeys: MutableList<K> = ArrayList()

    val all: List<V>
        get() {
            return arrayKeys.map { this[it]!! }
        }

    override fun putAll(from: Map<out K, V>) {
        for ((key, value) in from) {
            super.put(key, value)
            arrayKeys.add(key)
        }
    }

    override fun put(key: K, value: V): V? {
        val valueToReturn = super.put(key, value)
        arrayKeys = ArrayList(keys)
        return valueToReturn
    }

    fun getIndex(key: K): Int {
        return arrayKeys.indexOf(key)
    }

    fun getAtIndex(index: Int): V? {
        return get(arrayKeys[index])
    }

    fun getKeyAtIndex(index: Int): K {
        return arrayKeys[index]
    }

    fun getKeyWithValue(valueToSearch: V): K? {
        return arrayKeys.firstOrNull { get(it) === valueToSearch }
    }

    override fun remove(key: K): V? {
        val valueReturn = super.remove(key)
        arrayKeys = ArrayList(keys)
        return valueReturn
    }

    fun getArrayKeys(): List<K>? {
        return arrayKeys
    }

    override fun clear() {
        for (key in arrayKeys) {
            remove(key)
        }
        arrayKeys = ArrayList()
    }

    override val size: Int
        get() = arrayKeys.size
}