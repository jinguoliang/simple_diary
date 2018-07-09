package com.empty.jinux.simplediary.applock

import android.content.Context
import android.os.CountDownTimer
import com.empty.jinux.baselibaray.log.logi
import com.empty.jinux.simplediary.config.ConfigManager

class AppLockImplement(val context: Context, var config: ConfigManager) : AppLockManager {
    var locked: Boolean = true

    private val countDownTimer: CountDownTimer = object : CountDownTimer(5 * 1000, 1000) {
        override fun onFinish() {
            logi("finish", "locker")

            locked = true
        }

        override fun onTick(millisUntilFinished: Long) {
            logi("lock tick: $millisUntilFinished", "locker")
        }

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
