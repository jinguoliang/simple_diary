package com.empty.jinux.simplediary.util

import android.widget.TextView

fun TextView.getLineForCursor(): Int {
    return layout.getLineForOffset(selectionEnd)
}