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
        val editor = getMEditor()
        val cursorDrawable = getCursorDrawable(editor)

        val line = layout.getLineForOffset(pos)
        val start = layout.getLineStart(line)
        val end = layout.getLineEnd(line)
        if (text.getSpans(start, end, ParagrahEndLineSpan::class.java).isEmpty() || (pos == text.length) && text[pos - 1] == '\n') {
            cursorDrawable.level = 9000
        } else {
            cursorDrawable.level = 6500
        }
    }

    private fun getCursorDrawable(editor: Any): Drawable {
        val cursorArray = getMCursorDrawable(editor)
        if (cursorArray[0] !is ClipDrawable) {
            cursorArray[0] = ResourcesCompat.getDrawable(resources, R.drawable.edit_text_cursor, null)
        }
        return cursorArray[0]!!
    }

    private fun getMCursorDrawable(editor: Any): Array<Drawable?> {
        val mCursorDrawableFeild = editor.javaClass.getDeclaredField("mCursorDrawable")
        mCursorDrawableFeild.isAccessible = true
        return mCursorDrawableFeild.get(editor) as Array<Drawable?>
    }

    private fun getMEditor(): Any {
        val mEditorFeild = TextView::class.java.getDeclaredField("mEditor")
        mEditorFeild.isAccessible = true
        return mEditorFeild.get(this)
    }

}