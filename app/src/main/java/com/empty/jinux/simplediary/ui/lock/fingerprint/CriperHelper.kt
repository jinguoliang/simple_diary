package com.empty.jinux.simplediary.ui.lock.fingerprint

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

@RequiresApi(Build.VERSION_CODES.M)
class CriperHelper {

    companion object {
        const val ANDROID_KEY_STORE = "AndroidKeyStore"

        fun createKey(keyName: String): SecretKey? {
            // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
            // for your flow. Use of keys is necessary if you need to know if the set of enrolled
            // fingerprints has changed.
            try {

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

        fun setupCipher(): Cipher {
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

        fun getAndroidKeyGenerator(): KeyGenerator? {
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

        fun getAndroidKeyStore(): KeyStore? {
            try {
                return KeyStore.getInstance(ANDROID_KEY_STORE)
            } catch (e: KeyStoreException) {
                throw RuntimeException("Failed to get an instance of KeyStore", e)
            }
        }
    }
}