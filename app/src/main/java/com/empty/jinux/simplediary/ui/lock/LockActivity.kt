package com.empty.jinux.simplediary.ui.lock

import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.applock.AppLockManager
import com.empty.jinux.simplediary.config.ConfigManager
import com.empty.jinux.simplediary.databinding.ActivityLockBinding
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.lock.fingerprint.FingerprintHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LockActivity : AppCompatActivity() {

    @Inject
    lateinit var appLockManager: AppLockManager

    @Inject
    lateinit var config: ConfigManager

    @Inject
    lateinit var mReporter: Reporter
    private lateinit var binding: ActivityLockBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPasswordInputView()
        initFingerprint()
    }

    private fun setupPasswordInputView() {
        binding.password.addTextChangedListener(object : TextWatcher {
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
    }

    private lateinit var fingerprintHelper: FingerprintHelper

    private fun initFingerprint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val fingerprintMgr = getSystemService(FingerprintManager::class.java)
            fingerprintHelper = FingerprintHelper(
                    fingerprintMgr,
                    object : FingerprintHelper.Callback {
                        val fingerprintImageColorChanger = ImageColorChanger(binding.fingerPrintIcon)
                        override fun onAuthenticated() {
                            unlockApp()
                            reportUnlockTime("fingerprint")
                            fingerprintImageColorChanger.changeColorTemp(R.color.green)
                        }

                        override fun onFailed() {
                            fingerprintImageColorChanger.changeColorTemp(R.color.colorAccent)
                        }

                        override fun onError(errorCode: Int, msg: String) {
                            showFingerprintMessage(msg)
                        }

                        override fun onHelper(msg: String) {
                            showFingerprintMessage(msg)
                        }


                        override fun onStart() {
                            fingerprintImageColorChanger.changeColor(R.color.colorPrimaryDark)
                        }
                    }
            )

        }
    }

    private fun showFingerprintMessage(msg: String) {
        binding.fingerPrintMessage.text = msg
        binding.fingerPrintMessage.visibility = View.VISIBLE
    }

    private fun unlockApp() {
        appLockManager.unlock()
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
            binding.fingerPrintMessage.visibility = View.INVISIBLE
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

class ImageColorChanger(val imageView: ImageView) {
    private var currentColorRes: Int = 0

    fun changeColor(@ColorRes color: Int, isTemp: Boolean = false) {
        if (!isTemp) {
            currentColorRes = color
        }

        if (color == 0) {
            ImageViewCompat.setImageTintList(imageView, null)
        } else {
            ImageViewCompat.setImageTintList(imageView, ResourcesCompat.getColorStateList(imageView.resources, color, null))
        }
    }

    fun changeColorDelay(@ColorRes color: Int, delay: Long) {
        imageView.postDelayed({
            changeColor(color)
        }, delay)
    }

    fun changeColorTemp(@ColorRes color: Int) {
        val ori = currentColorRes
        changeColor(color, true)
        changeColorDelay(ori, 2000)
    }
}