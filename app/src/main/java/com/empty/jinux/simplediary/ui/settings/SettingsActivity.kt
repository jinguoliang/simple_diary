package com.empty.jinux.simplediary.ui.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.empty.jinux.simplediary.util.ActivityUtils


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.addFragmentToActivity(supportFragmentManager, SettingsFragment(), android.R.id.content)
    }

}