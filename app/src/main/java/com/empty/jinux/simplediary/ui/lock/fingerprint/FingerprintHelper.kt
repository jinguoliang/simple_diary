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
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import com.empty.jinux.baselibaray.log.logd
import com.empty.jinux.simplediary.util.ThreadPools
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

@RequiresApi(Build.VERSION_CODES.M)
class FingerprintHelper
internal constructor(private val fingerprintMgr: FingerprintManager,
                     private val callback: Callback
) : FingerprintManager.AuthenticationCallback() {

    private val cryptoObject:FingerprintManager.CryptoObject

    init {
        cryptoObject = createCryptoObject()
    }

    private var cancellationSignal: CancellationSignal? = null
    private var selfCancelled = false

    private val isFingerprintAuthAvailable: Boolean
        get() = fingerprintMgr.isHardwareDetected && fingerprintMgr.hasEnrolledFingerprints()

    fun startListening() {
        if (!isFingerprintAuthAvailable) return

        cancellationSignal = CancellationSignal()
        selfCancelled = false

        fingerprintMgr.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    private fun createCryptoObject(): FingerprintManager.CryptoObject {
        val key = createKey(DEFAULT_KEY_NAME)

        val defaultCipher = setupCipher()
        defaultCipher.init(Cipher.ENCRYPT_MODE, key)

        return FingerprintManager.CryptoObject(defaultCipher)
    }

    fun stopListening() {
        cancellationSignal?.also {
            selfCancelled = true
            it.cancel()
        }
        cancellationSignal = null
    }

    private fun setupCipher(): Cipher {
        val defaultCipher: Cipher
        try {
            val cipherString = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
            defaultCipher = Cipher.getInstance(cipherString)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchPaddingException ->
                    throw RuntimeException("Failed to get an instance of Cipher", e)
                else -> throw e
            }
        }
        return defaultCipher
    }


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

    private fun getAndroidKeyStore(): KeyStore? {
        try {
            return KeyStore.getInstance(ANDROID_KEY_STORE)
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to get an instance of KeyStore", e)
        }
    }

    private fun getAndroidKeyGenerator(): KeyGenerator? {
        try {
            return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchProviderException ->
                    throw RuntimeException("Failed to get an instance of KeyGenerator", e)
                else -> throw e
            }
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with a fingerprint.
     *
     * @param keyName the name of the key to be created
     */
    private fun createKey(keyName: String): SecretKey? {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of enrolled
        // fingerprints has changed.
        try {
            val keyStore = getAndroidKeyStore()
            keyStore?.load(null)

            val keyProperties = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val builder = KeyGenParameterSpec.Builder(keyName, keyProperties)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
//                    .setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)

            val keyGenerator = getAndroidKeyGenerator()
            return keyGenerator?.run {
                init(builder.build())
                generateKey()
            }
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is InvalidAlgorithmParameterException,
                is CertificateException,
                is IOException -> throw RuntimeException(e)
                else -> throw e
            }
        }
    }

    interface Callback {
        fun onAuthenticated()
        fun onFailed()
        fun onError(errorCode: Int, msg: String)
        fun onHelper(msg: String)
        fun onStart()
    }

    companion object {
        const val SUCCESS_DELAY_MILLIS: Long = 300
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val DEFAULT_KEY_NAME = "test"

    }
}
