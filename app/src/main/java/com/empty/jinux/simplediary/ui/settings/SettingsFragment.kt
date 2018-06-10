package com.empty.jinux.simplediary.ui.settings

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.CheckBoxPreference
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }

    @Inject
    lateinit var mReporter: Reporter

    private var mIsConfirmEnableLock: Boolean = false

    private fun showLockPasswordSetDialog(checkBoxPreference: CheckBoxPreference) {
        val dialog = Dialog(activity!!)
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
            mIsConfirmEnableLock = false
            dialog.cancel()
            checkBoxPreference.isChecked = false
            mReporter.reportClick("password_set_dialog_cancel")
        }

        dialog.positiveButton.setOnClickListener {
            mIsConfirmEnableLock = true
            dialog.dismiss()
            checkBoxPreference.isChecked = true
            mReporter.reportClick("password_set_dialog_ok")
        }
        dialog.show()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        onLockChecked()

    private fun onLockChecked() {
        val checkBoxPreference = findPreference(getString(R.string.pref_lock_enable)) as CheckBoxPreference
        checkBoxPreference.setOnPreferenceChangeListener { preference, newValue ->
            val checked = newValue as Boolean
            mReporter.reportClick(preference.key, "" + checked)
            if (checked && needConfirmWithInputPassword()) {
                // open lock
                showLockPasswordSetDialog(checkBoxPreference)
                false
            } else {
                mIsConfirmEnableLock = false
                true
            }
        }
    }

    private fun needConfirmWithInputPassword() = !mIsConfirmEnableLock
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

