package com.empty.jinux.simplediary.ui.diarydetail.fragment

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.empty.jinux.baselibaray.loge
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.util.ParagrahEndLineSpan

class MEditText : EditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)

        try {
            adjustCursorHeight(selStart)
        } catch (e: Exception) {
            loge("adjustCursorHeight failed: ${Log.getStackTraceString(e)}")
        }
    }

    private fun adjustCursorHeight(pos: Int) {
        // first time
        if (layout == null) return

        val editor: Any = reflectFeild(TextView::class.java, "mEditor")
        val cursorDrawables: Array<Drawable?> = editor.reflectFeild(editor.javaClass, "mCursorDrawable")

        val cursorDrawable = getCursorDrawable(cursorDrawables)

        val line = layout.getLineForOffset(pos)
        val start = layout.getLineStart(line)
        val end = layout.getLineEnd(line)
        val isParagraphEnd = text.getSpans(start, end, ParagrahEndLineSpan::class.java).isNotEmpty()
        // 本来只要判断是段尾行就行，但是当最后一行为空时，也被认为是段尾，所以需排除
        // 这是　getSpans　的原因
        if (isParagraphEnd && !((pos == text.length) && text[pos - 1] == '\n')) {
            cursorDrawable.level = 5800
        } else {
            cursorDrawable.level = 9000
        }
    }

    private fun getCursorDrawable(cursorArray: Array<Drawable?>): Drawable {
        if (cursorArray[0] !is ClipDrawable) {
            cursorArray[0] = ResourcesCompat.getDrawable(resources, R.drawable.edit_text_cursor, null)
        }
        return cursorArray[0]!!
    }

    private inline fun <reified T, reified D> D.reflectFeild(clazz: Class<D>, fieldName: String): T {
        val field = clazz.getDeclaredField(fieldName)
        field.isAccessible = true
        return field.get(this) as T
    }

}