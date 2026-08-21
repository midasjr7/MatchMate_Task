package com.example.matchmate.di

import android.util.Log
import com.example.matchmate.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {
    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        val handler = CoroutineExceptionHandler { _, error ->
            if (BuildConfig.DEBUG) {
                Log.e("MatchMate", "Uncaught application task failure", error)
            } else {
                Log.e("MatchMate", "Uncaught application task failure")
            }
        }
        return CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)
    }
}
