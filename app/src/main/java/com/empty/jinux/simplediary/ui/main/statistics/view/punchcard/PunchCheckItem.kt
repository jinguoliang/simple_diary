package com.empty.jinux.simplediary.ui.main.statistics.view.punchcard

class PunchCheckItem(
        val data: Any,
        val state: PunchCheckState)

enum class PunchCheckState {
    STATE_CHECKED, STATE_MISSED, STATE_NEED_CHECKED
}


