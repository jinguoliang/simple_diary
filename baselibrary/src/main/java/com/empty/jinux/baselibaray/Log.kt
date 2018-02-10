package com.empty.jinux.baselibaray

import android.content.ContentValues.TAG
import android.util.Log

/**
 * Created by Jinux on 2018/2/10.
 *
 * 打印日志
 * 为什么有它：
 * 1. 方便调用，默认 TAG 为 BaseLibrary
 * 2. 添加 Debug 开关，只在debug模式打印
 * 3. 可以调整打印日志的信息格式
 */

val DEFAULT_TAG = "BaseLibrary"
val LOG_DEBUG = BuildConfig.DEBUG

fun logd(o: Any): Unit {
    if (LOG_DEBUG) {
        Log.d(DEFAULT_TAG, formatMessage(o.toString()))
    }
}

private fun formatMessage(msg: String): String {
    return msg
}



