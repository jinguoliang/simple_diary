package com.empty.jinux.simplediary.util

import android.graphics.Color
import android.text.Spannable
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.MetricAffectingSpan
import android.widget.EditText
import java.util.regex.Pattern


fun String.getFirstLine(limitWidth: Int = 20): String {
    val firstLine = split("\n")[0]
    return firstLine.substring(0 until Math.min(firstLine.length, limitWidth))
}

private val span = BackgroundColorSpan(Color.CYAN)

fun EditText.adjustParagraphSpace() {
    val s = text
    text.getSpans(0, text.length, ParagrahEndLineSpan::class.java).forEach {
        text.removeSpan(it)
    }

    val newLineIndexes = s.findNewLines()
    newLineIndexes.forEach { newLinePos: Int ->
        addParagraphEndSpan(newLinePos)
    }
}

private fun EditText.addParagraphEndSpan(pos: Int) {
    val lineStart = layout.getLineStart(layout.getLineForOffset(pos))
    text.setSpan(ParagrahEndLineSpan(), lineStart, pos + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
}

fun CharSequence.findNewLines(): List<Int> {
    val positions = mutableListOf<Int>()
    val matcher = Pattern.compile("\n").matcher(this)
    while (matcher.find()) {
        positions.add(matcher.start())
    }
    return positions
}

class ParagrahEndLineSpan : MetricAffectingSpan() {
    override fun updateMeasureState(tp: TextPaint) {
        tp.baselineShift = 50
    }

    override fun updateDrawState(tp: TextPaint) {
    }

}