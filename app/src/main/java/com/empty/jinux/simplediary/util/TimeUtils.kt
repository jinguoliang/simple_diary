package com.empty.jinux.simplediary.util

import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.data.DiaryContent
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jingu on 2018/2/21.
 *
 * Time format utils
 */

fun formatTime(t: Long): String {
    val formater = SimpleDateFormat("yyyy/MM/dd HH:mm")
    return formater.format(t)
}

fun formatDateWithWeekday(t: Long): String {
    val formater = SimpleDateFormat("M月d日 E")
    return formater.format(t)
}

fun Diary.formatCreatedTime(): String {
    val createdTime = meta.createdTime
    if (createdTime == 0L) {
        return ""
    }
    return formatTime(createdTime)
}

fun DiaryContent.formatDisplayTime(): String {
    val displayTime = displayTime
    if (displayTime == 0L) {
        return ""
    }
    return formatDateWithWeekday(displayTime)
}


fun Long.weekStartTime(): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

    return calendar.timeInMillis
}

fun Long.formatToYeayWeek(): String {
    val formater = SimpleDateFormat("yyyy年  第 w 周")
    return formater.format(this)
}

fun Long.formatToWeekday(): String {
    val formater = SimpleDateFormat("E")
    return formater.format(this)
}

fun Long.formatToTime(): String {
    val formater = SimpleDateFormat("H:mm")
    return formater.format(this)
}


