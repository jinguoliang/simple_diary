package com.empty.jinux.simplediary.applock

import android.content.Context
import android.text.TextUtils
import com.empty.jinux.simplediary.config.ConfigManager

class AppLockImplement(val context: Context, var config: ConfigManager) : AppLockManager {
    var locked = false

    override fun isLock(): Boolean {
        return locked
    }

    override fun notifyLock() {
        locked = true
    }

    override fun unlock(password: String): Boolean {
        val success = TextUtils.equals(password, config.getString("password"))
        locked = !success
        return success
    }
}