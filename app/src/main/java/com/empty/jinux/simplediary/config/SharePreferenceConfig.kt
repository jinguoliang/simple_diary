package com.empty.jinux.simplediary.config

import android.content.Context
import androidx.preference.PreferenceManager

class SharePreferenceConfig(context: Context) : ConfigManager {
    override fun <T> get(key: String, defaultValue: T): T {
        when (defaultValue) {
            is String -> {
                return sharedPreferences.getString(key, defaultValue) as T
            }
            is Boolean -> {
                return sharedPreferences.getBoolean(key, defaultValue) as T
            }
            is Int -> {
                return sharedPreferences.getInt(key, defaultValue) as T
            }
            else -> {
                throw IllegalArgumentException("error argument")
            }
        }
    }

    override fun <T> put(key: String, value: T) {
        when (value) {
            is String -> {
                sharedPreferencesEditor.putString(key, value)
            }
            is Boolean -> {
                sharedPreferencesEditor.putBoolean(key, value)
            }
            is Int -> {
                sharedPreferencesEditor.putInt(key, value)
            }
        }
        sharedPreferencesEditor.apply()
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val sharedPreferencesEditor = sharedPreferences.edit()

}