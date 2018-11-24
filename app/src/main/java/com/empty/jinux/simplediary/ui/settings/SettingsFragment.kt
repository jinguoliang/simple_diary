package com.empty.jinux.simplediary.ui.settings

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceManager
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.utils.TextWatcherAdapter
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.backup.GoogleDriverBackup
import com.empty.jinux.simplediary.data.backup.GoogleDriverBackup.Companion.REQUEST_CODE_CREATION
import com.empty.jinux.simplediary.data.backup.GoogleDriverBackup.Companion.REQUEST_CODE_OPENING
import com.empty.jinux.simplediary.data.backup.GoogleDriverBackup.Companion.REQUEST_CODE_SIGN_IN
import com.empty.jinux.simplediary.report.Reporter
import com.google.android.gms.drive.DriveId
import com.google.android.gms.drive.OpenFileActivityOptions
import kotlinx.android.synthetic.main.dialog_app_lock_set_password.*
import java.io.File
import javax.inject.Inject

class SettingsFragment : DaggerPreferenceFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
    }

    @Inject
    lateinit var mReporter: Reporter

    @Inject
    lateinit var mBackupManager: BackupManager

    private var mIsConfirmEnableLock: Boolean = false

    private fun showLockPasswordSetDialog(checkBoxPreference: CheckBoxPreference) {
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.dialog_app_lock_set_password)
        val passwordChecker = object : TextWatcherAdapter() {
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
            PreferenceManager.getDefaultSharedPreferences(activity).edit {
                putString(getString(R.string.pref_lock_password), dialog.newPassword.text.toString())
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

    private fun onBackupToLocalClick() {
        mReporter.reportClick("backup_to_local")
        mBackupManager.performLocalBackup()
    }

    private fun onRestoreFromLocalClick() {
        mReporter.reportClick("restore_from_local")
        mBackupManager.performLocalRestore()
    }

    private fun onBackupToRemoteClick() {
        mBackupManager.remote.performBackup("test")
    }

    private fun onRestorFromRemoteClick() {
        mBackupManager.remote.performImport(File("test"))
    }

    private fun onPreferenceClick(@StringRes key: Int, onClickListener: () -> Unit) {
        findPreference(getString(key)).setOnPreferenceClickListener { onClickListener();true }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loge("request = $requestCode result = $resultCode", "JIN")
        when (requestCode) {

            REQUEST_CODE_SIGN_IN -> {
                Log.i(TAG, "Sign in request code")
                // Called after user is signed in.
//                if (resultCode == Activity.RESULT_OK) {
//                    mBackupManager.performLocalBackup()
//                }
            }

            REQUEST_CODE_CREATION ->
                // Called after a file is saved to Drive.
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Backup successfully saved.")
                    Toast.makeText(activity, "Backup successufly loaded!", Toast.LENGTH_SHORT).show()
                }

            REQUEST_CODE_OPENING -> if (resultCode == Activity.RESULT_OK && data != null) {
                val driveId = data.getParcelableExtra<DriveId>(
                        OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID)
                loge("driveId = $driveId")
                (mBackupManager.remote as GoogleDriverBackup).mOpenItemTaskSource.setResult(driveId)
            } else {
                (mBackupManager.remote as GoogleDriverBackup).mOpenItemTaskSource.setException(RuntimeException("Unable to open file"))
            }
        }
    }
}

