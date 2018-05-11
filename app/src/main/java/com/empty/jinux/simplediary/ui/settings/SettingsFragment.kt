package com.empty.jinux.simplediary.ui.settings

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.CheckBoxPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.edit
import com.empty.jinux.simplediary.R
import kotlinx.android.synthetic.main.dialog_app_lock_set_password.*

class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "pref_app_lock_enable") {
            if (sharedPreferences.getBoolean(key, false)) {
                // open lock
                showLockPasswordSetDialog()
            }
        }
    }

    private fun showLockPasswordSetDialog() {
        val dialog = Dialog(activity)
        dialog.setTitle(R.string.app_lock_set_password)
        dialog.setContentView(R.layout.dialog_app_lock_set_password)
        val passwordChecker: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.apply {
                    dialog.positiveButton.isEnabled = this.toString().length == 6
                            && dialog.passwordConfirm.text.toString() == dialog.newPassword.text.toString()
                }
            }

        }
        dialog.newPassword.addTextChangedListener(passwordChecker)
        dialog.passwordConfirm.addTextChangedListener(passwordChecker)
        dialog.negativeButton.setOnClickListener {
            dialog.cancel()
            PreferenceManager.getDefaultSharedPreferences(activity).edit {
                putBoolean("pref_app_lock_enable", false)
            }

            val checkbox = preferenceManager.findPreference("pref_app_lock_enable") as CheckBoxPreference
            checkbox.isChecked = false

        }
        dialog.positiveButton.setOnClickListener {
            dialog.dismiss()
            PreferenceManager.getDefaultSharedPreferences(activity).edit {
                putString("pref_app_lock_password", dialog.newPassword.text.toString())
            }
        }
        dialog.show()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }


}

