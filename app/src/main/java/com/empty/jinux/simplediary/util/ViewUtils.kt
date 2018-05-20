package com.empty.jinux.simplediary.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

var View.layoutHeight: Int
    get() = layoutParams.height
    set(h: Int) {
        layoutParams = layoutParams.apply { height = h }
    }

fun View.showInputMethod() {
    val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    im?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun View.hideInputMethod() {
    val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    im?.hideSoftInputFromWindow(this.windowToken, 0)
}