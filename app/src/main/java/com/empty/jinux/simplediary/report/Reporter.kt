package com.empty.jinux.simplediary.report

import android.os.Bundle
import androidx.annotation.Size
import com.empty.jinux.simplediary.report.app.*

interface Reporter {
    fun reportEvent(@Size(min = 1L, max = 40L) event: String, args: Bundle = Bundle.EMPTY)

    fun reportClick(where: String) {
        reportEvent(EVENT_CLICK, Bundle().apply {
            putString(ARG_WHERE, where)
        })
    }

    fun reportClick(where: String, value: String? = null) {
        reportEvent(EVENT_CLICK, Bundle().apply {
            putString(ARG_WHERE, where)
            if (value != null) {
                putString(ARG_VALUE, value)
            }
        })
    }

    fun reportDrawerClosed() {
        reportEvent(EVENT_DRAWER_CLOSED)
    }

    fun reportDrawerOpened() {
        reportEvent(EVENT_DRAWER_OPENED)
    }

    fun reportCount(what: String, count: Int) {
        reportEvent(EVENT_COUNT, Bundle().apply {
            putString(ARG_WHAT, what)
            putInt(ARG_VALUE, count)
        })
    }
}