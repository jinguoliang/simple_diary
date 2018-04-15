package com.empty.jinux.simplediary.ui.diarydetail.fragment

import com.empty.jinux.simplediary.R

object MyWeatherIcons {
    private val DEFAULT_WEATHER = R.drawable.ic_location
    private val MAP_ICON_TO_MY_ICON = mapOf(
            "01" to R.drawable.ic_01d,
            "02" to R.drawable.ic_02d,
            "03" to R.drawable.ic_03d,
            "09" to R.drawable.ic_09d,
            "10" to R.drawable.ic_10d,
            "11" to R.drawable.ic_11d,
            "13" to R.drawable.ic_13d,
            "50" to R.drawable.ic_50d
    )

    fun mapToMyIcon(icon: String): Int {
        return MAP_ICON_TO_MY_ICON[icon.substring(0 .. 1)] ?: DEFAULT_WEATHER
    }

    fun getAllMyIcon(): List<Int> {
        return MAP_ICON_TO_MY_ICON.values.toList()
    }

    fun getIconIndex(icon: String): Int {
        return getAllMyIcon().indexOf(mapToMyIcon(icon))
    }

    fun getIconByIndex(position: Int): String {
        return MAP_ICON_TO_MY_ICON.keys.toList()[position]
    }
}