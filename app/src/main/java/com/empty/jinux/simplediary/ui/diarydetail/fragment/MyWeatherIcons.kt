package com.empty.jinux.simplediary.ui.diarydetail.fragment

import com.empty.jinux.simplediary.R

object MyWeatherIcons {
    private const val DEFAULT_WEATHER = R.drawable.ic_location
    private val IconList = arrayOf(R.drawable.ic_01d,
            R.drawable.ic_02d,
            R.drawable.ic_03d,
            R.drawable.ic_09d,
            R.drawable.ic_10d,
            R.drawable.ic_11d,
            R.drawable.ic_13d,
            R.drawable.ic_50d
    )
    private val IconCodeList = arrayOf("01", "02", "03",
            "09", "10", "11",
            "13", "50")

    private val Names = arrayOf("Sun", "Few Cloud", "Cloud", "Rain",
            "Sun Rain", "Thunderstorm", "Snow", "Mist")

    private val MAP_ICON_TO_MY_ICON = mapOf(
            IconCodeList[0] to IconList[0],
            IconCodeList[1] to IconList[1],
            IconCodeList[2] to IconList[2],
            IconCodeList[3] to IconList[3],
            IconCodeList[4] to IconList[4],
            IconCodeList[5] to IconList[5],
            IconCodeList[6] to IconList[6],
            IconCodeList[7] to IconList[7]
    )

    private fun mapToMyIcon(icon: String): Int {
        return MAP_ICON_TO_MY_ICON[icon.substring(0..1)] ?: DEFAULT_WEATHER
    }

    fun getAllMyIcon(): List<Int> {
        return IconList.toList()
    }

    fun getIconIndex(icon: String): Int {
        return IconList.indexOf(mapToMyIcon(icon))
    }

    fun getIconByIndex(position: Int): String {
        return IconCodeList.toList()[position]
    }

    fun getWeatherName(position: Int): String {
        return Names[position]
    }
}