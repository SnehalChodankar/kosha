package com.kosha.app.domain.backup

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupCryptoManager {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128
    private const val IV_LENGTH_BYTE = 12
    private const val SALT_LENGTH_BYTE = 16
    private const val ITERATION_COUNT = 65536
    private const val KEY_LENGTH_BIT = 256

    /**
     * Generates a random 6-digit PIN.
     */
    fun generatePin(): String {
        val random = SecureRandom()
        val pin = random.nextInt(1000000)
        return String.format("%06d", pin)
    }

    private fun deriveKey(pin: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(pin.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BIT)
        val secretKey = factory.generateSecret(spec)
        return SecretKeySpec(secretKey.encoded, "AES")
    }

    /**
     * Encrypts the payload using AES-GCM.
     * Returns: [Salt (16)] + [IV (12)] + [Ciphertext + Auth Tag]
     */
    fun encryptBackup(jsonData: String, pin: String): ByteArray {
        val salt = ByteArray(SALT_LENGTH_BYTE).apply { SecureRandom().nextBytes(this) }
        val iv = ByteArray(IV_LENGTH_BYTE).apply { SecureRandom().nextBytes(this) }

        val key = deriveKey(pin, salt)
        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)

        val cipherText = cipher.doFinal(jsonData.toByteArray(Charsets.UTF_8))

        // Prepend salt and IV
        return salt + iv + cipherText
    }

    /**
     * Decrypts the backup file. Throws AEADBadTagException or Exception if PIN is wrong.
     */
    fun decryptBackup(fileData: ByteArray, pin: String): String {
        if (fileData.size < SALT_LENGTH_BYTE + IV_LENGTH_BYTE) {
            throw IllegalArgumentException("Invalid backup file: Too short.")
        }

        val salt = fileData.copyOfRange(0, SALT_LENGTH_BYTE)
        val iv = fileData.copyOfRange(SALT_LENGTH_BYTE, SALT_LENGTH_BYTE + IV_LENGTH_BYTE)
        val cipherText = fileData.copyOfRange(SALT_LENGTH_BYTE + IV_LENGTH_BYTE, fileData.size)

        val key = deriveKey(pin, salt)
        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        val plainTextBytes = cipher.doFinal(cipherText)
        return String(plainTextBytes, Charsets.UTF_8)
    }
}
