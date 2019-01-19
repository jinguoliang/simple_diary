package com.empty.jinux.simplediary.ui.diarydetail.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.widget.EditText
import com.empty.jinux.baselibaray.utils.getScaleImage
import org.jetbrains.anko.dip

class DiaryPictureManager(val context: Context) {
    fun generateImage(data: Uri, diaryContent: EditText): Bitmap? {
        val edgeWidth = context.dip(10)
        val space = context.dip(10)
        val targetWidth = (diaryContent.width - diaryContent.paddingLeft - diaryContent.paddingRight - 2 * space - 2 * edgeWidth) / 2

        return context.getScaleImage(data, targetWidth)?.let { ori ->
            val w = ori.width
            val h = ori.height
            val edgeWidth = (edgeWidth.toFloat() * w / targetWidth).toInt()
            val space = (space.toFloat() * w / targetWidth).toInt()

            Bitmap.createBitmap(w + 2 * edgeWidth + 2 * space, h + 2 * edgeWidth + 2 * space, Bitmap.Config.ARGB_4444)
                    .also {
                        Canvas(it).run {
                            drawColor(Color.TRANSPARENT)
                            drawRect(space.toFloat(), space.toFloat(), (space + 2 * edgeWidth + w).toFloat(), (space + 2 * edgeWidth + h).toFloat(), Paint().also { it.color = Color.WHITE })
                            drawBitmap(ori, space + edgeWidth.toFloat(), space + edgeWidth.toFloat(), null)
                        }
                    }
        }
    }
}