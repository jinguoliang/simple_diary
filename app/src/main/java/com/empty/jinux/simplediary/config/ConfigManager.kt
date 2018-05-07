package com.empty.jinux.simplediary.config

/**
 * Config read/save
 */
interface ConfigManager {
    fun getString(key: String, defaultValue: String = ""): String
    fun putString(key: String, value: String)
}