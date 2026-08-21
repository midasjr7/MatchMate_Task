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
import javax.inject.Inject
import javax.inject.Singleton
import com.example.matchmate.security.DataCipher

interface MatchRepository {
    fun observeMatches(): Flow<List<Match>>
    suspend fun refresh()
    suspend fun updateStatus(id: String, status: MatchStatus)
}

@Singleton
class MatchRepositoryImpl @Inject constructor(
    private val database: MatchDatabase,
    private val api: RandomUserApi,
    private val cipher: DataCipher,
) : MatchRepository {
    private val dao = database.matchDao()

    override fun observeMatches(): Flow<List<Match>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain(cipher) } }

    override suspend fun refresh() {
        val remote = api.getMatches().results
        database.withTransaction {
            val statuses = dao.getAll().associate { it.id to it.status }
            dao.upsertAll(remote.map { it.toEntity(statuses[it.login.uuid], cipher) })
            dao.deleteProfilesNotIn(remote.map { it.login.uuid })
        }
    }

    override suspend fun updateStatus(id: String, status: MatchStatus) {
        check(dao.updateStatus(id, status.name) == 1) { "Match no longer exists" }
    }
}

internal fun UserDto.toEntity(existingStatus: String?, cipher: DataCipher) = MatchEntity(
    id = login.uuid,
    name = cipher.encrypt("${name.first} ${name.last}"),
    age = dob.age,
    location = cipher.encrypt(listOf(location.city, location.state, location.country).joinToString(", ")),
    email = cipher.encrypt(email),
    phone = cipher.encrypt(phone),
    imageUrl = picture.large,
    status = existingStatus ?: MatchStatus.PENDING.name,
)
