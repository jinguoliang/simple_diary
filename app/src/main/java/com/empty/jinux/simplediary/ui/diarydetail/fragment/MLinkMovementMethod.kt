package com.empty.jinux.simplediary.ui.diarydetail.fragment

import android.text.Layout
import android.text.Selection
import android.text.Spannable
import android.text.method.ArrowKeyMovementMethod
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.MotionEvent
import android.widget.TextView

class MLinkMovementMethod : LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        val action = event.action

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            var x = event.x.toInt()
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop

            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            val links = buffer.getSpans(off, off, ClickableSpan::class.java)

            if (links.isNotEmpty()) {
                if (action == MotionEvent.ACTION_UP) {
                    links[0].onClick(widget)
                } else if (action == MotionEvent.ACTION_DOWN) {
//                                Selection.setSelection(buffer,
//                                        buffer.getSpanStart(links[0]),
//                                        buffer.getSpanEnd(links[0]))
                }
                return true
            } else {
                Selection.removeSelection(buffer)
            }
        }

        return super.onTouchEvent(widget, buffer, event)
    }
}

class MArrowKeyMethod : ArrowKeyMovementMethod() {
    override fun left(widget: TextView, buffer: Spannable): Boolean {
        val text = buffer
        val start = Selection.getSelectionStart(text)
        val end = Selection.getSelectionEnd(text)
        val layout = widget.layout

        if (start != end) {
            Selection.setSelection(text, chooseHorizontal(layout, -1, start, end))
            return true
        } else {
            val to = layout.getOffsetToLeftOf(end)
            if (text.getSpans(to, to, ImageSpan::class.java).isNotEmpty()) {
                return false
            }
            if (to != end) {
                Selection.setSelection(text, to)
                return true
            }
        }

        return false
    }

    private fun chooseHorizontal(layout: Layout, direction: Int,
                                 off1: Int, off2: Int): Int {
        val line1 = layout.getLineForOffset(off1)
        val line2 = layout.getLineForOffset(off2)

        if (line1 == line2) {
            // same line, so it goes by pure physical direction

            val h1 = layout.getPrimaryHorizontal(off1)
            val h2 = layout.getPrimaryHorizontal(off2)

            return if (direction < 0) {
                // to left

                if (h1 < h2)
                    off1
                else
                    off2
            } else {
                // to right

                if (h1 > h2)
                    off1
                else
                    off2
            }
        } else {
            // different line, so which line is "left" and which is "right"
            // depends upon the directionality of the text

            // This only checks at one end, but it's not clear what the
            // right thing to do is if the ends don't agree.  Even if it
            // is wrong it should still not be too bad.
            val line = layout.getLineForOffset(off1)
            val textdir = layout.getParagraphDirection(line)

            return if (textdir == direction)
                Math.max(off1, off2)
            else
                Math.min(off1, off2)
        }
    }

    override fun right(widget: TextView, buffer: Spannable): Boolean {
        val text = buffer
        val start = Selection.getSelectionStart(text)
        val end = Selection.getSelectionEnd(text)
        val layout = widget.layout

        if (start != end) {
            Selection.setSelection(text, chooseHorizontal(layout, 1, start, end))
            return true
        } else {
            val to = layout.getOffsetToRightOf(end)
            if (text.getSpans(to, to, ImageSpan::class.java).isNotEmpty()) {
                return false
            }
            if (to != end) {
                Selection.setSelection(text, to)
                return true
            }
        }

        return false
    }

    override fun up(widget: TextView, buffer: Spannable): Boolean {
        val text = buffer
        val start = Selection.getSelectionStart(text)
        val end = Selection.getSelectionEnd(text)
        val layout = widget.layout

        if (start != end) {
            val min = Math.min(start, end)
            val max = Math.max(start, end)

            Selection.setSelection(text, min)

            return !(min == 0 && max == text.length)

        } else {
            val line = layout.getLineForOffset(end)

            if (line > 0) {
                val move = if (layout.getParagraphDirection(line) == layout.getParagraphDirection(line - 1)) {
                    val h = layout.getPrimaryHorizontal(end)
                    layout.getOffsetForHorizontal(line - 1, h)
                } else {
                    layout.getLineStart(line - 1)
                }
                if (text.getSpans(move, move, ImageSpan::class.java).isEmpty()) {
                    Selection.setSelection(text, move)
                } else {

                }
                return true
            } else if (end != 0) {
                Selection.setSelection(text, 0)
                return true
            }
        }

        return false
    }

    override fun down(widget: TextView, buffer: Spannable): Boolean {
        val text = buffer
        val start = Selection.getSelectionStart(text)
        val end = Selection.getSelectionEnd(text)
        val layout = widget.layout

        if (start != end) {
            val min = Math.min(start, end)
            val max = Math.max(start, end)

            Selection.setSelection(text, max)

            return !(min == 0 && max == text.length)

        } else {
            val line = layout.getLineForOffset(end)

            if (line < layout.lineCount - 1) {
                val move = if (layout.getParagraphDirection(line) == layout.getParagraphDirection(line + 1)) {
                    val h = layout.getPrimaryHorizontal(end)
                    layout.getOffsetForHorizontal(line + 1, h)
                } else {
                    layout.getLineStart(line + 1)
                }
                if (text.getSpans(move, move, ImageSpan::class.java).isEmpty()) {
                    Selection.setSelection(text, move)
                } else {
                    widget.scrollBy(0, widget.textSize.toInt())
                }
                return true
            } else if (end != 0) {
                Selection.setSelection(text, 0)
                return true
            }
        }

        return false    }

}