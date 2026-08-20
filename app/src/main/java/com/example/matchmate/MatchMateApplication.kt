package com.example.matchmate

import android.app.Application
import androidx.room.Room
import com.example.matchmate.data.MatchRepository
import com.example.matchmate.data.local.MatchDatabase
import com.example.matchmate.data.remote.RandomUserApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MatchMateApplication : Application() {
    val repository: MatchRepository by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        val api = Retrofit.Builder()
            .baseUrl("https://randomuser.me/")
            .client(OkHttpClient.Builder().addInterceptor(logging).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RandomUserApi::class.java)
        val database = Room.databaseBuilder(this, MatchDatabase::class.java, "matchmate.db").build()
        MatchRepository(database, api)
    }
}
