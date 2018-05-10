package com.empty.jinux.simplediary.ui.settings

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.empty.jinux.simplediary.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}
