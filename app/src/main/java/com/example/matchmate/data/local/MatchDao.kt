package com.example.matchmate.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY name")
    fun observeAll(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches")
    suspend fun getAll(): List<MatchEntity>

    @Upsert
    suspend fun upsertAll(matches: List<MatchEntity>)

    @Query("DELETE FROM matches WHERE id NOT IN (:profileIds)")
    suspend fun deleteProfilesNotIn(profileIds: List<String>)

    @Query("UPDATE matches SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String): Int
}
