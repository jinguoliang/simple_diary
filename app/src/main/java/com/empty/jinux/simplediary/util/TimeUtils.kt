package com.empty.jinux.simplediary.util

import com.empty.jinux.simplediary.data.Diary
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

fun Diary.formatCreatedTime(): String {
    if (createTime == 0L) {
        return ""
    }
    return formatTime(createTime)
}
