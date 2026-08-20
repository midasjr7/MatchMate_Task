package com.example.matchmate.data

import androidx.room.withTransaction
import com.example.matchmate.data.local.MatchDatabase
import com.example.matchmate.data.local.MatchEntity
import com.example.matchmate.data.remote.RandomUserApi
import com.example.matchmate.data.remote.UserDto
import com.example.matchmate.domain.Match
import com.example.matchmate.domain.MatchStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MatchRepository(
    private val database: MatchDatabase,
    private val api: RandomUserApi,
) {
    private val dao = database.matchDao()

    fun observeMatches(): Flow<List<Match>> =
        dao.observeAll().map { rows -> rows.map(MatchEntity::toDomain) }

    suspend fun refresh() {
        val remote = api.getMatches().results
        database.withTransaction {
            val statuses = dao.getAll().associate { it.id to it.status }
            dao.upsertAll(remote.map { it.toEntity(statuses[it.login.uuid]) })
        }
    }

    suspend fun updateStatus(id: String, status: MatchStatus) {
        check(dao.updateStatus(id, status.name) == 1) { "Match no longer exists" }
    }
}

internal fun UserDto.toEntity(existingStatus: String?) = MatchEntity(
    id = login.uuid,
    name = "${name.first} ${name.last}",
    age = dob.age,
    location = listOf(location.city, location.state, location.country).joinToString(", "),
    email = email,
    phone = phone,
    imageUrl = picture.large,
    status = existingStatus ?: MatchStatus.PENDING.name,
)
