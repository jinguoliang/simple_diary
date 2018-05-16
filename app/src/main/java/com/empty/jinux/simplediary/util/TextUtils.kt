package com.empty.jinux.simplediary.util

import android.text.SpannableStringBuilder
import android.text.style.SuperscriptSpan
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_demo.*

fun String.getFirstLine(limitWidth: Int = 20): String {
    val firstLine = split("\n")[0]
    return firstLine.substring(0 until Math.min(firstLine.length, limitWidth))
}

fun EditText.adjustParagraphSpace(s: CharSequence?) {
    if (s?.contains("\n") == true) {
        val returnIndexs = s.mapIndexed { index, c -> if (c == '\n') index else -1 }.filterNot { it == -1 }
        returnIndexs.forEachIndexed { index, i ->
            val line = layout.getLineForOffset(i)
            val lineStart = layout.getLineStart(line)
            val spans = text.getSpans(lineStart, i, SuperscriptSpan::class.java)
            if (spans.isEmpty()) {
                text.setSpan(SuperscriptSpan(),
                        lineStart,
                        i,
                        SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
    }
}