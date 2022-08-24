package com.empty.jinux.simplediary.ui.settings

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.StringRes
import androidx.core.content.edit
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.databinding.DialogAppLockSetPasswordBinding
import com.empty.jinux.simplediary.report.Reporter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }

    @Inject
    lateinit var mReporter: Reporter

    @Inject
    lateinit var mBackupManager: BackupManager

    private var mIsConfirmEnableLock: Boolean = false

    private fun showLockPasswordSetDialog(checkBoxPreference: CheckBoxPreference) {
        val dialog = Dialog(context!!)
        val binding = DialogAppLockSetPasswordBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        val passwordChecker: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.apply {
                    binding.positiveButton.isEnabled = this.toString().length == 6
                            && binding.passwordConfirm.text.toString() == binding.newPassword.text.toString()
                }
            }

        }
        binding.newPassword.addTextChangedListener(passwordChecker)
        binding.passwordConfirm.addTextChangedListener(passwordChecker)
        binding.negativeButton.setOnClickListener {
            mIsConfirmEnableLock = false
            dialog.cancel()
            checkBoxPreference.isChecked = false
            mReporter.reportClick("password_set_dialog_cancel")
        }

        binding.positiveButton.setOnClickListener {
            mIsConfirmEnableLock = true
            dialog.dismiss()
            checkBoxPreference.isChecked = true
            PreferenceManager.getDefaultSharedPreferences(activity).edit {
                putString(
                    getString(R.string.pref_lock_password),
                    binding.newPassword.text.toString()
                )
            }
            mReporter.reportClick("password_set_dialog_ok")
        }
        dialog.show()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        onLockChecked()

        onPreferenceClick(R.string.pref_back_to_local) {
            onBackupToLocalClick()
        }

        onPreferenceClick(R.string.pref_restore_from_local) {
            onRestoreFromLocalClick()
        }

//        onPreferenceClick(R.string.pref_back_to_google_driver) {
//            onBackupToRemoteClick()
//        }
//
//        onPreferenceClick(R.string.pref_restore_from_google_driver) {
//            onRestorFromRemoteClick()
//        }
    }

    private fun onLockChecked() {
        val checkBoxPreference =
            findPreference<CheckBoxPreference>(getString(R.string.pref_lock_enable))
        checkBoxPreference?.setOnPreferenceChangeListener { preference, newValue ->
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

    private fun onBackupToLocalClick() {
        mReporter.reportClick("backup_to_local")
        mBackupManager.performLocalBackup()
    }

    private fun onRestoreFromLocalClick() {
        mReporter.reportClick("restore_from_local")
        mBackupManager.performLocalRestore()
    }

    private fun onBackupToRemoteClick() {
    }

    private fun onRestorFromRemoteClick() {
    }

    private fun onPreferenceClick(@StringRes key: Int, onClickListener: () -> Unit) {
        findPreference<Preference>(getString(key))?.setOnPreferenceClickListener { onClickListener();true }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    private val TAG: String = "settings"

}

