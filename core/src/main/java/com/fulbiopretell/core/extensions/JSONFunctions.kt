package com.fulbiopretell.retoyape.core.extensions

import org.json.JSONArray
import org.json.JSONObject

operator fun JSONArray.iterator(): Iterator<JSONObject> = (0 until length())
        .asSequence()
        .filter { get(it) is JSONObject }
        .map { get(it) as JSONObject }
        .iterator()

fun JSONArray.stringIterator(): Iterator<String> = (0 until length())
        .asSequence()
        .filter { get(it) is String }
        .map { get(it) as String }
        .iterator()