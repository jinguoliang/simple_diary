/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.empty.jinux.simplediary.ui.lock.fingerprint

import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import com.empty.jinux.baselibaray.log.logd
import com.empty.jinux.baselibaray.thread.ThreadPools
import javax.crypto.Cipher

@RequiresApi(Build.VERSION_CODES.M)
class FingerprintHelper
internal constructor(private val fingerprintMgr: FingerprintManager,
                     private val callback: Callback
) {

//    private val cryptoObject = createCryptoObject()

    private var cancellationSignal: CancellationSignal? = null
    private var selfCancelled = false

//    private fun createCryptoObject(): FingerprintManager.CryptoObject {
//        val key = CriperHelper.createKey(DEFAULT_KEY_NAME)
//
//        val defaultCipher = CriperHelper.setupCipher()
//
//        try {
//            defaultCipher.init(Cipher.ENCRYPT_MODE, key)
//        } catch (e: Throwable) {
//            return createCryptoObject()
//        }
//
//        return FingerprintManager.CryptoObject(defaultCipher)
//    }

    private val isFingerprintAuthAvailable: Boolean
        get() = fingerprintMgr.isHardwareDetected && fingerprintMgr.hasEnrolledFingerprints()

    fun startListening() {
        if (!isFingerprintAuthAvailable) return

        cancellationSignal = CancellationSignal()
        selfCancelled = false

        fingerprintMgr.authenticate(null, cancellationSignal, 0, listener, null)
    }

    fun stopListening() {
        cancellationSignal?.also {
            selfCancelled = true
            it.cancel()
        }
        cancellationSignal = null
    }


    val listener = object : FingerprintManager.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
            ThreadPools.postOnUIDelayed(SUCCESS_DELAY_MILLIS) { callback.onAuthenticated() }
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            logd("onAuthenticationFailed")
            ThreadPools.postOnUIDelayed(SUCCESS_DELAY_MILLIS) { callback.onFailed() }
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            logd("onAuthenticationError: $errorCode $errString")
            ThreadPools.postOnUIDelayed(SUCCESS_DELAY_MILLIS) { callback.onError(errorCode, errString.toString()) }
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            super.onAuthenticationHelp(helpCode, helpString)
            logd("onAuthenticationHelp: $helpCode $helpString")
            ThreadPools.postOnUIDelayed(SUCCESS_DELAY_MILLIS) { callback.onHelper(helpString.toString()) }
        }
    }


    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with a fingerprint.
     *
     * @param keyName the name of the key to be created
     */

    interface Callback {
        fun onAuthenticated()
        fun onFailed()
        fun onError(errorCode: Int, msg: String)
        fun onHelper(msg: String)
        fun onStart()
    }

    companion object {
        const val SUCCESS_DELAY_MILLIS: Long = 100
        private const val DEFAULT_KEY_NAME = "fingerprint"

    }
}
