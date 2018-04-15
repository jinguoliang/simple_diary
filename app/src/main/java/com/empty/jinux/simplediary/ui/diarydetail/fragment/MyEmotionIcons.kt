package com.empty.jinux.simplediary.ui.diarydetail.fragment

import com.empty.jinux.simplediary.R

object MyEmotionIcons {
    private val MAP_ICON_TO_MY_ICON = arrayListOf(
            R.drawable.ic_emotion_haha,
            R.drawable.ic_emotion_smile,
            R.drawable.ic_emotion_normal,
            R.drawable.ic_emotion_sad,
            R.drawable.ic_emotion_cry
    )

    fun getEmotion(index: Int): Int {
        return MAP_ICON_TO_MY_ICON[index]
    }

    fun getAllMyIcon(): List<Int> {
        return MAP_ICON_TO_MY_ICON
    }

    fun getIconIndex(icon: Int): Int {
        return getAllMyIcon().indexOf(icon)
    }
}