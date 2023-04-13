package com.fulbiopretell.retoyape.core.extensions

import org.json.JSONArray
import org.json.JSONObject

fun JSONObject?.getBooleanOrDefault(key: String, defaultValue: Boolean = false): Boolean =
        tryOrDefault({ this?.getBoolean(key) ?: defaultValue }, defaultValue)

fun JSONObject?.getIntOrDefault(key: String, defaultValue: Int = 0): Int =
        tryOrDefault({ this?.getInt(key) ?: defaultValue }, defaultValue)

fun JSONObject?.getStringOrDefault(key: String, defaultValue: String? = null): String? =
        tryOrDefault({ this?.getString(key) ?: defaultValue }, defaultValue)

fun JSONObject?.getDoubleOrDefault(key: String, defaultValue: Double? = null): Double? =
        tryOrDefault({ this?.getDouble(key) ?: defaultValue }, defaultValue)

fun JSONObject?.getJSONObjectOrDefault(key: String, defaultValue: JSONObject? = null): JSONObject? =
        tryOrDefault({ this?.getJSONObject(key) ?: defaultValue }, defaultValue)

fun JSONObject?.getJSONArrayOrDefault(key: String, defaultValue: JSONArray? = null): JSONArray? =
        tryOrDefault({ this?.getJSONArray(key) ?: defaultValue }, defaultValue)
