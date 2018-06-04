package com.empty.jinux.simplediary.applock

/**
 * Need password when enter app
 */
interface AppLockManager {
    fun isLock(): Boolean
    fun notifyLock()
    fun unlock(): Boolean
    fun clearCountDown()
}