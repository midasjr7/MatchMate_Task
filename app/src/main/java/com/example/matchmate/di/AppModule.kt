package com.example.matchmate.di

import android.content.Context
import androidx.room.Room
import com.example.matchmate.BuildConfig
import com.example.matchmate.data.local.MatchDatabase
import com.example.matchmate.data.remote.RandomUserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MatchDatabase =
        Room.databaseBuilder(context, MatchDatabase::class.java, "matchmate.db").build()

    @Provides
    @Singleton
    fun provideApi(): RandomUserApi {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        return Retrofit.Builder()
            .baseUrl(BuildConfig.RANDOM_USER_BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor(logging).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RandomUserApi::class.java)
    }
}
