package com.empty.jinux.simplediary.ui.lock

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.widget.toast
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.applock.AppLockManager
import com.empty.jinux.simplediary.config.ConfigManager
import com.empty.jinux.simplediary.report.Reporter
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
                if (appLockManager.unlock(s.toString(), config.get("pref_app_lock_password", ""))) {
                    toast("app lock unlock!!", Toast.LENGTH_LONG)
                    finish()
                    reportUnlockTime()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

    }

    private fun reportUnlockTime() {
        val elapse = (System.currentTimeMillis() - mInputPasswordBeginTime) / 1000
        mReporter.reportEvent("unlock", Bundle().apply { putLong("elapse", elapse) })
    }

    private var mInputPasswordBeginTime: Long = 0L

    override fun onResume() {
        super.onResume()
        mInputPasswordBeginTime = System.currentTimeMillis()
    }

    override fun onBackPressed() {
        // forbid back pressed
    }
}