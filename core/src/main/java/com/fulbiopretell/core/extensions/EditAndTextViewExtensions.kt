package com.fulbiopretell.retoyape.core.extensions

import android.graphics.Typeface
import android.widget.EditText
import android.widget.TextView


//region EditText
/**
 * Converts text of [EditText] text(CharSequence) to string and also trim it.
 *
 * @return text
 */
fun EditText.cleanText() = this.text.toString().trim()

/**
 * Converts text of editText text(CharSequence) to string, trim it.
 * and then check if the result is empty.
 *
 * @return if the [cleanText] is empty
 */
fun EditText.textIsEmpty() : Boolean = this.cleanText().isEmpty()
//endregion
//region TextView
/**
 * Converts text of [TextView] text(CharSequence) to string and also trim it.
 *
 * @return text
 */
fun TextView.textToString() = this.text.toString()

/**
 * Set the style of [TextView] to bold
 * */
fun TextView.bold() = this.setTypeface(null, Typeface.BOLD)
//endregion