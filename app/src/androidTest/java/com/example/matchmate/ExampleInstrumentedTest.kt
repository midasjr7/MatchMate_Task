package com.example.matchmate

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun appContextUsesExpectedPackage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.matchmate", context.packageName)
    }
}
