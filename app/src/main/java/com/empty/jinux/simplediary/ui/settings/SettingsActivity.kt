package com.empty.jinux.simplediary.ui.settings

import android.os.Bundle
import com.empty.jinux.simplediary.R

class SettingsActivity : DaggerPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

}