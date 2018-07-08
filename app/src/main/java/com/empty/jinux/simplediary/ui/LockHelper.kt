package com.empty.jinux.simplediary.ui

import android.content.Context
import com.empty.jinux.simplediary.applock.AppLockManager
import com.empty.jinux.simplediary.config.ConfigManager
import com.empty.jinux.simplediary.ui.lock.LockActivity
import com.empty.jinux.baselibaray.thread.ThreadPools
import org.jetbrains.anko.startActivity
import javax.inject.Inject


class LockHelper @Inject constructor() {
    private var lockEnable: Boolean = false

    fun onStart(context: Context) {
        lockEnable = config.get("pref_app_lock_enable", false)
        if (lockEnable && mAppLock.isLock()) {
            context.startActivity<LockActivity>()
        }

        ThreadPools.postOnUIDelayed(500) {
            mAppLock.clearCountDown()
        }
    }

    fun onStop() {
        if (lockEnable) {
        if (lockEnable && !mAppLock.isLock()) {
            mAppLock.notifyLock()
        }
    }

    @Inject
    lateinit var mAppLock: AppLockManager

    @Inject
    lateinit var config: ConfigManager


}