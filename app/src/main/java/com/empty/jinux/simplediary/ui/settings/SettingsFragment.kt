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
import com.empty.jinux.simplediary.report.Reporter
import kotlinx.android.synthetic.main.dialog_app_lock_set_password.*
import javax.inject.Inject

class SettingsFragment : DaggerPreferenceFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var mReporter: Reporter

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            "pref_app_lock_enable" -> sharedPreferences.getBoolean(key, false).let { checked ->
                mReporter.reportClick(key, checked.toString())
                if (checked) {
                    // open lock
                    PreferenceManager.getDefaultSharedPreferences(activity).edit {
                        putBoolean("pref_app_lock_enable", false)
                    }
                    showLockPasswordSetDialog()
                }
            }
        }
    }

    private fun showLockPasswordSetDialog() {
        val dialog = Dialog(activity)
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
            mReporter.reportClick("password_set_dialog_cancel")

            val checkbox = preferenceManager.findPreference("pref_app_lock_enable") as CheckBoxPreference
            checkbox.isChecked = false

        }
        dialog.positiveButton.setOnClickListener {
            dialog.dismiss()
            mReporter.reportClick("password_set_dialog_ok")

            PreferenceManager.getDefaultSharedPreferences(activity).edit {
                putString("pref_app_lock_password", dialog.newPassword.text.toString())
                putBoolean("pref_app_lock_enable", true)
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

