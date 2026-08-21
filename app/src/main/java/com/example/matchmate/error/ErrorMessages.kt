package com.example.matchmate.error

import java.io.IOException
import retrofit2.HttpException

fun Throwable.toUserMessage(): String = when (this) {
    is IOException -> "You're offline. Showing saved matches."
    is HttpException -> when (code()) {
        429 -> "Too many requests. Please try again shortly."
        in 500..599 -> "The profile service is temporarily unavailable."
        else -> "The profile request couldn't be completed."
    }
    is SecurityException -> "Secure profile data couldn't be accessed."
    else -> "Something went wrong. Please try again."
}
