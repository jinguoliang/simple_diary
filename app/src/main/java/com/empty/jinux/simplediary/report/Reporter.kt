package com.empty.jinux.simplediary.report

import android.os.Bundle
import android.support.annotation.Size
import com.empty.jinux.simplediary.report.app.ARG_SELECTED_VALUE
import com.empty.jinux.simplediary.report.app.ARG_WHERE
import com.empty.jinux.simplediary.report.app.EVENT_CLICK

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
                putString(ARG_SELECTED_VALUE, value)
            }
        })
    }
}