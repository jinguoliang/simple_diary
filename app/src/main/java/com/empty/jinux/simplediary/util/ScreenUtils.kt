package com.empty.jinux.simplediary.util

import android.content.Context
import org.jetbrains.anko.displayMetrics

fun Context.getScreenWidth(): Int {
    return displayMetrics.widthPixels
}

fun Context.getScreenHeight(): Int {
    return displayMetrics.heightPixels
}