package com.empty.jinux.simplediary.applock

import android.content.Context
import com.empty.jinux.baselibaray.log.logi
import com.empty.jinux.baselibaray.utils.CountDownTimer
import com.empty.jinux.simplediary.config.ConfigManager

class AppLockImplement(val context: Context, var config: ConfigManager) : AppLockManager {
    var locked: Boolean = true

    private val countDownTimer = CountDownTimer.countDownToDo(5 * 1000) {
        logi("finish", "locker")
        locked = true
    }

    override fun isLock(): Boolean {
        return locked
    }

    override fun clearCountDown() {
        countDownTimer.cancel()
    }

    override fun notifyLock() {
        countDownTimer.start()
    }

    override fun unlock(): Boolean {
        locked = false
        return locked
    }
}
