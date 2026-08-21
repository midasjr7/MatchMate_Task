package com.example.matchmate.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeystoreDataCipher @Inject constructor() : DataCipher {
    private val key: SecretKey
        get() {
            val keyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }
            return (keyStore.getKey(KEY_ALIAS, null) as? SecretKey) ?: createKey()
        }

    override fun encrypt(plainText: String): String {
        if (plainText.isEmpty() || plainText.startsWith(PREFIX)) return plainText
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val payload = cipher.iv + cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        return PREFIX + Base64.encodeToString(payload, Base64.NO_WRAP)
    }

    override fun decrypt(storedValue: String): String {
        if (!storedValue.startsWith(PREFIX)) return storedValue
        val payload = Base64.decode(storedValue.removePrefix(PREFIX), Base64.NO_WRAP)
        require(payload.size > IV_LENGTH_BYTES) { "Invalid encrypted profile value" }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_LENGTH_BITS, payload.copyOfRange(0, IV_LENGTH_BYTES)))
        return String(cipher.doFinal(payload.copyOfRange(IV_LENGTH_BYTES, payload.size)), StandardCharsets.UTF_8)
    }

    private fun createKey(): SecretKey {
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            ).setKeySize(256)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build(),
        )
        return generator.generateKey()
    }

    private companion object {
        const val KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "matchmate.profile.data.v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val PREFIX = "enc:v1:"
        const val IV_LENGTH_BYTES = 12
        const val TAG_LENGTH_BITS = 128
    }
}
