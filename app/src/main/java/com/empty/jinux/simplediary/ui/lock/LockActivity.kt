package com.empty.jinux.simplediary.ui.lock

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.applock.AppLockManager
import com.empty.jinux.simplediary.util.showToast
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_lock.*
import javax.inject.Inject

class LockActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var appLockManager: AppLockManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_lock)

        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (appLockManager.unlock(s.toString())) {
                    showToast("app lock unlock!!")
                    finish()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

    }

    override fun onBackPressed() {
        // forbid back pressed
    }
}