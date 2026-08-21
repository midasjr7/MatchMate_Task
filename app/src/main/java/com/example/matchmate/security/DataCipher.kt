package com.example.matchmate.security

interface DataCipher {
    fun encrypt(plainText: String): String
    fun decrypt(storedValue: String): String
}
