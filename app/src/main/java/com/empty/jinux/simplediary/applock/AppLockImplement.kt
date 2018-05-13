package com.empty.jinux.simplediary.applock

import android.content.Context
import android.os.CountDownTimer
import android.text.TextUtils
import com.empty.jinux.simplediary.config.ConfigManager

class AppLockImplement(val context: Context, var config: ConfigManager) : AppLockManager {
    var locked: Boolean = true

    val countDownTimer: CountDownTimer = object : CountDownTimer(5 * 1000, 5 * 1000 + 1) {
        override fun onFinish() {
            locked = true
        }

        override fun onTick(millisUntilFinished: Long) {
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

    override fun unlock(password: String, input: String): Boolean {
        val success = TextUtils.equals(password, input)
        locked = !success
        return success
    }
}
