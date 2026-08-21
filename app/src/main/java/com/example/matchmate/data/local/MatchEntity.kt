package com.example.matchmate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.matchmate.domain.Match
import com.example.matchmate.domain.MatchStatus
import com.example.matchmate.domain.Gender
import com.example.matchmate.security.DataCipher

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
    val page: Int = 1,
    val gender: String = Gender.UNKNOWN.name,
) {
    fun toDomain(cipher: DataCipher) = Match(
        id = id,
        name = cipher.decrypt(name),
        age = age,
        location = cipher.decrypt(location),
        email = cipher.decrypt(email),
        phone = cipher.decrypt(phone),
        imageUrl = imageUrl,
        gender = runCatching { Gender.valueOf(gender) }.getOrDefault(Gender.UNKNOWN),
        status = runCatching { MatchStatus.valueOf(status) }.getOrDefault(MatchStatus.PENDING),
    )
}
