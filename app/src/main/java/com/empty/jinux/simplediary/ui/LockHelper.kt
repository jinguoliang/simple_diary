package com.empty.jinux.simplediary.ui

import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat.startActivity
import com.empty.jinux.baselibaray.log.logd
import com.empty.jinux.baselibaray.log.logi
import com.empty.jinux.simplediary.applock.AppLockManager
import com.empty.jinux.simplediary.config.ConfigManager
import com.empty.jinux.simplediary.ui.lock.LockActivity
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.utils.startActivity
import javax.inject.Inject


class LockHelper @Inject constructor() {
    private var lockEnable: Boolean = false

    fun onStart(context: Context) {
        logi("check lock: ${mAppLock.isLock()}", "locker")

        lockEnable = config.get("pref_app_lock_enable", false)
        if (lockEnable && mAppLock.isLock()) {
            (context as Activity).startActivity<LockActivity>()
        }

        ThreadPools.postOnUIDelayed(500) {
            mAppLock.clearCountDown()
        }
    }

    fun onStop() {
        logi("start to lock", "locker")

        if (lockEnable && !mAppLock.isLock()) {
            mAppLock.notifyLock()
        }
    }

    @Inject
    lateinit var mAppLock: AppLockManager

    @Inject
    lateinit var config: ConfigManager


}