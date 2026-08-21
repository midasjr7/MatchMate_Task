package com.example.matchmate.di

import com.example.matchmate.data.MatchRepository
import com.example.matchmate.data.MatchRepositoryImpl
import com.example.matchmate.security.DataCipher
import com.example.matchmate.security.KeystoreDataCipher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {
    @Binds
    @Singleton
    abstract fun bindMatchRepository(implementation: MatchRepositoryImpl): MatchRepository

    @Binds
    @Singleton
    abstract fun bindDataCipher(implementation: KeystoreDataCipher): DataCipher
}
