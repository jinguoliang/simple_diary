package com.empty.jinux.simplediary.util

import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.data.DiaryContent
import java.text.SimpleDateFormat

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
