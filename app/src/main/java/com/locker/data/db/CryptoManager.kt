package com.locker.data.db

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManager {

    companion object {
        private const val KEY_ALIAS = "locker_master_key"
        private const val KEY_ALIAS_TEST = "locker_master_key_test"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    private fun getSecretKey(forTesting: Boolean = false): SecretKey {
        val alias = if (forTesting) KEY_ALIAS_TEST else KEY_ALIAS
        val existingKey = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createSecretKey(alias, forTesting)
    }

    private fun createSecretKey(alias: String, forTesting: Boolean): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val builder = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            
        if (!forTesting) {
            builder.setUserAuthenticationRequired(true)
                   .setUserAuthenticationValidityDurationSeconds(10)
        }

        keyGenerator.init(builder.build())
        return keyGenerator.generateKey()
    }

    fun getEncryptCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getSecretKey(forTesting = false))
        }
    }

    fun getDecryptCipher(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getSecretKey(forTesting = false), GCMParameterSpec(128, iv))
        }
    }

    fun getEncryptCipherForTestingBypass(): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getSecretKey(forTesting = true))
        }
    }

    fun getDecryptCipherForTestingBypass(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getSecretKey(forTesting = true), GCMParameterSpec(128, iv))
        }
    }
}
