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
    val formater = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    return formater.format(t)
}

fun formatDateWithWeekday(t: Long): String {
    val formater = SimpleDateFormat("M月d日 E", Locale.getDefault())
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
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    calendar.setToDayStart()

    return calendar.timeInMillis
}

fun Long.dayTime(): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    calendar.setToDayStart()

    return calendar.timeInMillis
}

fun Long.formatToYearWeek(): String {
    val format = SimpleDateFormat("w'th week' 'of' yyyy ", Locale.getDefault())
    return format.format(this)
}

fun Long.formatToWeekday(): String {
    val formater = SimpleDateFormat("E", Locale.getDefault())
    return formater.format(this)
}

fun Long.formatToTime(): String {
    val formater = SimpleDateFormat("H:mm", Locale.getDefault())
    return formater.format(this)
}

fun today(): Calendar {
    return Calendar.getInstance().setToDayStart()
}

private fun Calendar.setToDayStart(): Calendar {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return this
}

fun Calendar.toStringPretty(): String {
    return "${get(Calendar.YEAR)}年${get(Calendar.MONTH) + 1}月${get(Calendar.DAY_OF_MONTH)}日 "
}

fun Long.toCalendar(): Calendar {
    return Calendar.getInstance().also { it.timeInMillis = this }
}

operator fun Calendar.rangeTo(end: Calendar): CalendarRange {
    return CalendarRange(this, end)
}


