package com.empty.jinux.baselibaray.utils

import android.content.Context
import android.os.RemoteException
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import com.empty.jinux.baselibaray.log.logd

fun View.showInputMethod() {
    val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    val showed: Boolean = try {
        im?.showSoftInput(this@showInputMethod, InputMethodManager.SHOW_FORCED)!!
    } catch (e: RemoteException) {
        false
    }

    logd("showInputMethod: $showed")
}

fun View.hideInputMethod() {
    val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    im?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun ViewGroup.inflate(@LayoutRes res: Int, attach: Boolean): View {
    return LayoutInflater.from(context).inflate(res, this, attach)
}