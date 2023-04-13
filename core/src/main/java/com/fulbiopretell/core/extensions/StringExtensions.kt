package com.fulbiopretell.retoyape.core.extensions

import android.graphics.Typeface
import android.text.ParcelableSpan
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.AbsoluteSizeSpan
import android.text.style.UnderlineSpan
import java.util.StringTokenizer
import java.util.regex.Pattern

fun String.phoneFormat(): String {
    val input = this
    return when (input.length) {
        7 -> String.format("%s%s", input.substring(0, 3), input.substring(3, 7))
        10 -> String.format("(%s) %s %s", input.substring(0, 3), input.substring(3, 6), input.substring(6, 10))
        11 -> String.format("%s (%s) %s%s", input.substring(0, 1), input.substring(1, 4), input.substring(4, 7), input.substring(7, 11))
        12 -> String.format("+%s (%s) %s%s", input.substring(0, 2), input.substring(2, 5), input.substring(5, 8), input.substring(8, 12))
        else -> ""
    }
}

fun String.toSpannable(isBold: Boolean, color: Int): SpannableString =
        SpannableString(this).applySpans(listOf(0, this.length),
                ForegroundColorSpan(color),
                styleSpan = StyleSpan(Typeface.BOLD).takeIf { isBold }
                        ?: StyleSpan(Typeface.NORMAL))


fun String.countOccurrencesOf(token: String): Int = StringTokenizer(" $this ", token).countTokens() - 1

fun String.getIndexesAndReplaceChar(char: String, limit: Int): Pair<String, List<Int>> {
    var mutableString = this
    val indexes = mutableListOf<Int>()
    var index = mutableString.indexOf(char)
    while (index >= 0 || indexes.size < limit) {
        indexes.add(index)
        mutableString = mutableString.replaceFirst(char, "")
        index = mutableString.indexOf(char, index)
    }
    return Pair(mutableString, indexes)
}

fun SpannableString.applySpans(indexes: List<Int>,
                               colorSpan: ForegroundColorSpan? = null,
                               styleSpan: StyleSpan? = null,
                               strikeThroughSpan: StrikethroughSpan? = null,
                               sizeSpan: RelativeSizeSpan? = null,
                               absoluteSizeSpan: AbsoluteSizeSpan? = null,
                               underlineSpan: UnderlineSpan? = null): SpannableString {
    if (indexes.size > 1) {
        var index = 0
        while (index + 1 < indexes.size) {
            colorSpan?.let { this.setSpan(ForegroundColorSpan(it.foregroundColor), indexes[index], indexes[index + 1], 0) }
            styleSpan?.let { this.setSpan(StyleSpan(it.style), indexes[index], indexes[index + 1], 0) }
            underlineSpan?.let { this.setSpan(it, indexes[index], indexes[index + 1], 0) }
            strikeThroughSpan?.let { this.setSpan(StrikethroughSpan(), indexes[index], indexes[index + 1], 0) }
            sizeSpan?.let { this.setSpan(RelativeSizeSpan(it.sizeChange), indexes[index], indexes[index + 1], 0) }
            absoluteSizeSpan?.let { this.setSpan(AbsoluteSizeSpan(it.size), indexes[index], indexes[index + 1], 0) }
            index += 2
        }
    }
    return this
}

fun CharSequence.applySpans(vararg spans: ParcelableSpan): Spannable {
    var result: Spannable = SpannableString("")
    if (this.isNotEmpty()) {
        result = SpannableString(this)
        val limit = spans.size
        (0 until limit)
                .map { spans[it] }
                .forEach { result.setSpan(it, 0, this.length, 33) }
    }
    return result
}

fun String.getIndexesEnclosedSentences(startString: String, endString: String): Pair<String, List<Int>> {
    var mutableString = this
    val indexes = mutableListOf<Int>()
    while (mutableString.contains(startString) && mutableString.contains(endString)) {
        indexes.add(mutableString.indexOf(startString))
        mutableString = mutableString.replaceFirst(startString, "")
        indexes.add(mutableString.indexOf(endString))
        mutableString = mutableString.replaceFirst(endString, "")
    }
    return Pair(mutableString, indexes)
}

fun String.getSpannableEnclosedStringWithFormat(startEnclosed: String,
                                                endEnclosed: String,
                                                styleSpan: StyleSpan? = null,
                                                colorSpan: ForegroundColorSpan? = null,
                                                sizeSpan: RelativeSizeSpan? = null,
                                                underlineSpan: UnderlineSpan? = null): SpannableString {
    val (cleanMessage, indexes) = this.getIndexesEnclosedSentences(startEnclosed, endEnclosed)
    return SpannableString(cleanMessage).applySpans(indexes, styleSpan = styleSpan, underlineSpan = underlineSpan, colorSpan = colorSpan, sizeSpan = sizeSpan)
}

fun String.isUrl(): Boolean {
    val matches = Pattern.compile("^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?\$").matcher(this)
    return matches.find()
}

fun String.isProductUrlAmazon(): Boolean {
    return this.isUrl() && (this.contains("dp/") || this.contains("gp/"))
}

fun String.getLastPathSection(): String? {
    val matches = Pattern.compile("([^\\/]+)\$").matcher(this)
    return if (matches.find()) matches.group(0) else null
}

fun String.findAsin(): String {
    val match = Pattern.compile("(?:dp|o|gp|-|d)\\/(B[0-9]{2}[0-9A-Z]{7}|[0-9]{9}(?:X|[0-9]))").matcher(this)

    return if (match.find()) match.group(1) else ""
}

fun String.upperCaseFirstLetter(): String {
    var text = this
    if (text.isNotEmpty()) {
        text = text[0].toString().toUpperCase() + text.substring(1).toLowerCase()
    }
    return text
}

fun String.stringToArray(): Array<String> {
    return if (this.isNotBlank()) {
        this.trim()
                .split(",")
                .toTypedArray()
    } else {
        emptyArray()
    }
}
