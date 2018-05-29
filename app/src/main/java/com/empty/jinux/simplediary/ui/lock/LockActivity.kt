package com.empty.jinux.simplediary.ui.lock

import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.widget.toast
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.applock.AppLockManager
import com.empty.jinux.simplediary.config.ConfigManager
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.lock.fingerprint.FingerprintHelper
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_lock.*
import javax.inject.Inject

class LockActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var appLockManager: AppLockManager

    @Inject
    lateinit var config: ConfigManager

    @Inject
    lateinit var mReporter: Reporter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_lock)

        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = config.get("pref_app_lock_password", "")
                val input = s.toString()
                if (TextUtils.equals(password, input)) {
                    unlockApp()
                    reportUnlockTime("unlock")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        initFingerPrint()
    }

    private lateinit var fingerprintHelper: FingerprintHelper

    private fun initFingerPrint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintHelper = FingerprintHelper(
                    getSystemService(FingerprintManager::class.java),
                    object : FingerprintHelper.Callback {
                        override fun onAuthenticated() {
                            unlockApp()
                            reportUnlockTime("fingerprint")
                        }

                        override fun onError() {
                            toast("hello error").show()
                        }
                    }
            )
        }
    }

    private fun unlockApp() {
        appLockManager.unlock()
        toast("app lock unlock!!", Toast.LENGTH_LONG)
        finish()
    }

    private fun reportUnlockTime(name: String) {
        val elapse = (System.currentTimeMillis() - mInputPasswordBeginTime) / 1000
        mReporter.reportEvent(name, Bundle().apply { putLong("elapse", elapse) })
    }

    private var mInputPasswordBeginTime: Long = 0L

    override fun onResume() {
        super.onResume()
        mInputPasswordBeginTime = System.currentTimeMillis()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintHelper.startListening()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintHelper.stopListening()
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}