package com.example.matchmate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.matchmate.domain.Match
import com.example.matchmate.domain.MatchStatus

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val location: String,
    val email: String,
    val phone: String,
    val imageUrl: String,
    val status: String = MatchStatus.PENDING.name,
) {
    fun toDomain() = Match(
        id = id,
        name = name,
        age = age,
        location = location,
        email = email,
        phone = phone,
        imageUrl = imageUrl,
        status = runCatching { MatchStatus.valueOf(status) }.getOrDefault(MatchStatus.PENDING),
    )
}
