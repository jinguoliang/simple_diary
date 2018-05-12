package com.empty.jinux.simplediary.config

/**
 * Config read/save
 */
interface ConfigManager {
    fun <T> get(key: String, defaultValue: T): T
    fun <T> put(key: String, value: T)
}