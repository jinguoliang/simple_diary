package com.empty.jinux.simplediary.ui.diarydetail.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.text.Selection
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.EditText
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.log.logi
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.utils.adjustParagraphSpace
import com.empty.jinux.baselibaray.utils.compress
import com.empty.jinux.baselibaray.utils.dpToPx
import com.empty.jinux.simplediary.config.ConfigManager
import org.jetbrains.anko.toast
import java.io.File

class EditorFormator(val editor: MEditText, val mConfig: ConfigManager) {
    private var mPictureManager = DiaryPictureManager(editor.context!!)

    fun insertPictureMark(key: String) {
        val append = SpannableStringBuilder("\n[]($key)\n")
        editor.text.also {
            val selectStart = Selection.getSelectionStart(it)
            val selectEnd = Selection.getSelectionEnd(it)
            when {
                selectStart == -1 -> it.append(append)
                selectEnd == selectStart -> it.insert(selectStart, append)
                else -> it.replace(selectStart, selectEnd, append)
            }
        }
    }

    fun formatEditContent(editFontSize: Float) {
        ThreadPools.postOnUI {

            logi("formatEditContent adjust paragraph", "detail")
            editor.adjustParagraphSpace(editor.dpToPx(editFontSize / 2))
            editor.addPictureSpans()
            editor.adjustCursorHeightNoException()
        }
    }

    fun EditText.addPictureSpans() {
        val reg = "\\[(.*)]\\((.*)\\)".toRegex()
        reg.findAll(text).forEach {
            //            addPictureSpan(0, 43, Uri.parse(it.groupValues[2]))
            addPictureSpan(it.range.start, it.range.endInclusive, it.groupValues[2])
        }
    }

    private fun EditText.addPictureSpan(start: Int, end: Int, key: String) {
        val file = mConfig.get(key, "")
        if (text.getSpans(start, end + 1, ImageSpan::class.java).isNotEmpty()) return

        loadImage(file)?.run {
            BitmapDrawable(context.resources, this).also { drawable ->
                val width = editor.width - editor.paddingLeft - editor.paddingRight
                val height = drawable.intrinsicHeight / drawable.intrinsicWidth.toFloat() * width
                drawable.setBounds(0, 0, width, height.toInt()).also {
                    loge("bound = ${drawable.bounds} w = ${drawable.bounds.width()} h = ${drawable.bounds.height()}")
                }
            }
        }?.apply {
            val imageSpan = ImageSpan(this)
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View?) {
                    context?.toast("hello world")
                }

                override fun updateDrawState(ds: TextPaint?) {
                    ds?.bgColor = Color.CYAN
                }
            }
            loge("span text = ${text.subSequence(start, end + 1)}")
            text.setSpan(imageSpan, start, end + 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            text.setSpan(clickableSpan, start, end + 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
        }
    }

    private fun loadImage(file: String): Bitmap? {
        return BitmapFactory.decodeFile(file)
    }

    fun insertPictureMark(uri: Uri, onEnd: (() -> Unit)) {
        ThreadPools.postOnQuene {
            mPictureManager.generateImage(uri, editor)?.apply {
                val key = generateKey(uri)
                val path = "${getImageDir()}/$key"
                mConfig.put(key, path)
                compress(path)

                ThreadPools.postOnUI {
                    insertPictureMark(key)
                    onEnd()
                }
            }
        }
    }

    private fun generateKey(uri: Uri): String {
        return uri.hashCode().toString()
    }

    private fun getImageDir(): String {
        val imagesDir = "${editor.context!!.filesDir}/images".run { File(this) }
        if (!imagesDir.isDirectory) {
            imagesDir.mkdir()
        }
        return imagesDir.toString()
    }
}