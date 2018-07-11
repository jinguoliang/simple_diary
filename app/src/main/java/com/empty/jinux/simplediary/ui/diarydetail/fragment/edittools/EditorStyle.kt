package com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

class EditorStyle(val context: Context, val name: String) {
    companion object {
        val DEFAULT = "default"
        //        private val BACKGROUNDS = mapOf()
        val NAMES = arrayOf(
                DEFAULT,
                "zhuibo",
                "atom",
                "heiyaoshi",
                "meibao",
                "wumu",
                "qingyun")

        private val BACKGROUNDS = mapOf(
                NAMES[0] to 0xfff1ecb4,
                NAMES[1] to 0xffe94f89,
                NAMES[2] to 0xff313440,
                NAMES[3] to 0xff000000,
                NAMES[4] to 0xffd7f2fe,
                NAMES[5] to 0xff0d0d26,
                NAMES[6] to 0xff323b3e
        )
        private val FONT_COLORS = mapOf(
                NAMES[0] to 0xff000000,
                NAMES[1] to 0xff2f101b,
                NAMES[2] to 0xffceced2,
                NAMES[3] to 0xffd6d6d9,
                NAMES[4] to 0xff2c3235,
                NAMES[5] to 0xffcfcfd4,
                NAMES[6] to 0xffd6d8d8
        )
    }

    val background: Drawable
        get() {
            return ColorDrawable(BACKGROUNDS[name]!!.toInt())
        }

    val fontColor: Int
        get() {
            return FONT_COLORS[name]!!.toInt()
        }
}