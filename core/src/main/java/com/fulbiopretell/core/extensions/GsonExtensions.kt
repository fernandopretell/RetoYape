package com.fulbiopretell.retoyape.core.extensions

import com.google.gson.*

fun JsonObject?.getBooleanOrDefault(key: String, defaultValue: Boolean = false): Boolean =
        tryOrDefault({
            getJsonElementOrDefault(key)?.asBoolean ?: defaultValue
        }, defaultValue)

fun JsonObject?.getIntOrDefault(key: String, defaultValue: Int = 0): Int =
        tryOrDefault({
            getJsonElementOrDefault(key)?.asInt ?: defaultValue
        }, defaultValue)

fun JsonObject?.getLongOrDefault(key: String, defaultValue: Long? = null): Long? =
        tryOrDefault({
            getJsonElementOrDefault(key)?.asLong ?: defaultValue
        }, defaultValue)

fun JsonObject?.getDoubleOrDefault(key: String, defaultValue: Double? = null): Double? =
        tryOrDefault({
            getJsonElementOrDefault(key)?.asDouble ?: defaultValue
        }, defaultValue)

fun JsonObject?.getStringOrDefault(key: String, defaultValue: String? = null): String? =
        tryOrDefault({
            getJsonElementOrDefault(key)?.asString ?: defaultValue
        }, defaultValue)

fun JsonObject?.getFloatOrDefault(key: String, defaultValue: Float? = null): Float? =
        tryOrDefault({
            getJsonElementOrDefault(key)?.asFloat ?: defaultValue
        }, defaultValue)

fun JsonObject?.getJsonObjectOrDefault(key: String, defaultValue: JsonObject? = null): JsonObject? =
        tryOrDefault({
            getJsonElementOrDefault(key)?.asJsonObject ?: defaultValue
        }, defaultValue)

fun JsonObject?.getJsonArrayOrDefault(key: String, defaultValue: JsonArray? = null): JsonArray? =
        tryOrDefault({
            getJsonElementOrDefault(key)?.asJsonArray ?: defaultValue
        }, defaultValue)

fun JsonObject?.getJsonElementOrDefault(key: String, defaultValue: JsonElement? = null): JsonElement? =
        tryOrDefault({
            if (this?.get(key)?.isJsonNull == false) {
                this.get(key) ?: defaultValue
            } else {
                defaultValue
            }
        }, defaultValue)

fun <T> JsonElement?.fromJson(clazz: Class<T>): T? {
    try {
        return Gson().fromJson(JsonParser().parse(this.toString()), clazz)
    } catch (e: JsonSyntaxException) {
        return null
    }
}