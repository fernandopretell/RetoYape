package com.fulbiopretell.retoyape.core.extensions

import android.app.Activity
import android.content.Context
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.forEach

fun hideKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showKeyboardWithToggle(context: Context) {
    try {
        val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}

fun showKeyboard(context: Context, view: View) {
    val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view.requestFocus()
    keyboard.showSoftInput(view, 0)
}

fun hideKeyboard(activity: Activity) {
    activity.currentFocus?.let {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}

fun hideKeyboardSoftInputAlwaysHidden(activity: Activity) {
    activity.currentFocus?.let {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}

fun hideKeyboard(activity: Activity, token: IBinder) {
    activity.currentFocus?.let {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(token, 0)
    }
}

fun hideKeyboardOnTouch(view: View) {
    if (view !is EditText) {
        view.setOnTouchListener { touchedView, _ ->
            hideKeyboard(touchedView.context, touchedView)
            false
        }
    }
    if (view is ViewGroup) {
        view.forEach { hideKeyboardOnTouch(it) }
    }
}

fun hideKeyboardOnTouchIfItIsNotEditTextAndClearFocus(view: View, arrayOfEditTexts: Array<EditText>) {
    if (view !is EditText) {
        view.setOnTouchListener { touchedView, _ ->
            hideKeyboard(touchedView.context, touchedView)
            arrayOfEditTexts.forEach {
                it.clearFocus()
                hideKeyboardOnTouch(it)
            }
            false
        }
    }
    if (view is ViewGroup) {
        view.forEach { view ->
            if (view !is EditText) {
                hideKeyboardOnTouchIfItIsNotEditTextAndClearFocus(view, arrayOfEditTexts)
            }
        }
    }
}
