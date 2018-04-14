package com.empty.jinux.simplediary.ui.diarydetail.fragment

import com.empty.jinux.simplediary.R

object MyEmotionIcons {
    private val MAP_ICON_TO_MY_ICON = arrayListOf(
            R.drawable.ic_01d,
            R.drawable.ic_02d,
            R.drawable.ic_03d,
            R.drawable.ic_09d,
            R.drawable.ic_10d,
            R.drawable.ic_11d,
            R.drawable.ic_13d,
            R.drawable.ic_50d
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