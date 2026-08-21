package com.example.matchmate.error

import java.io.IOException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ErrorMessagesTest {
    @Test fun `network failures produce an offline message`() {
        assertEquals("You're offline. Showing saved matches.", IOException().toUserMessage())
    }

    @Test fun `rate limiting produces a retry later message`() {
        val error = HttpException(Response.error<Unit>(429, "".toResponseBody()))
        assertEquals("Too many requests. Please try again shortly.", error.toUserMessage())
    }

    @Test fun `unexpected failures do not expose internal details`() {
        assertEquals("Something went wrong. Please try again.", IllegalStateException("secret").toUserMessage())
    }
}
