package com.empty.jinux.simplediary.config

import android.content.Context
import org.jetbrains.anko.doAsync

class SharePreferenceConfig(context: Context) : ConfigManager {
    private val sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE)
    private val sharedPreferencesEditor = sharedPreferences.edit()

    override fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue)
    }

    override fun putString(key: String, value: String) {
        sharedPreferencesEditor.putString(key, value).apply()
    }
}