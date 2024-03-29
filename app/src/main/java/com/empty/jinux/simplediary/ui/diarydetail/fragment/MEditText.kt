package com.empty.jinux.simplediary.ui.diarydetail.fragment

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.simplediary.R
import com.empty.jinux.baselibaray.utils.ParagraphEndLineSpan
import com.empty.jinux.baselibaray.utils.getLineForCursor

class MEditText : androidx.appcompat.widget.AppCompatEditText {
    var mScrollParent: ScrollView? = null
    private var mEditVisibleHeight: Int = -1

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)

        loge("current end $selEnd", "jin")

        // first time
        if (layout == null) return
        adjustCursorHeightNoException()

        ThreadPools.postOnUI {
            adjustScrollPosition(mScrollParent, -1)
        }
    }


    fun adjustScrollPosition(scrollView: ScrollView?, editorVisibleAreaHeight: Int) {
        if (editorVisibleAreaHeight != -1) {
            mEditVisibleHeight = editorVisibleAreaHeight
        }

        if (layout == null || scrollView == null || mEditVisibleHeight == -1) {
            return
        }

        loge("edit visible height = $mEditVisibleHeight")
        val cursorLine = getLineForCursor()
        val cursorLineBottom = layout.getLineBottom(cursorLine)

        val cursorYOffset = cursorLineBottom - scrollView.scrollY

        if (cursorYOffset > mEditVisibleHeight) {
            val scroll = cursorYOffset - mEditVisibleHeight
            scrollView.post {
                scrollView.smoothScrollBy(0, scroll)
            }
        }
    }

    fun adjustCursorHeightNoException() {
        try {
            adjustCursorHeight(selectionEnd)
        } catch (e: Exception) {
            loge("adjustCursorHeight failed: ${Log.getStackTraceString(e)}")
        }
    }

    private fun adjustCursorHeight(pos: Int) {
        val editor: Any = reflectFeild(TextView::class.java, "mEditor")
        val cursorDrawables: Array<Drawable?> = editor.reflectFeild(editor.javaClass, "mCursorDrawable")

        val cursorDrawable = getCursorDrawable(cursorDrawables)

        val line = layout.getLineForOffset(pos)
        val start = layout.getLineStart(line)
        val end = layout.getLineEnd(line)
        val isParagraphEnd = text!!.getSpans(start, end, ParagraphEndLineSpan::class.java).isNotEmpty()
        // 本来只要判断是段尾行就行，但是当最后一行为空时，也被认为是段尾，所以需排除
        // 这是　getSpans　的原因
        if (isParagraphEnd && !((pos == text!!.length) && text!![pos - 1] == '\n')) {
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


}

public inline fun <reified T, reified D> D.reflectFeild(clazz: Class<D>, fieldName: String): T {
    val field = clazz.getDeclaredField(fieldName)
    field.isAccessible = true
    return field.get(this) as T
}