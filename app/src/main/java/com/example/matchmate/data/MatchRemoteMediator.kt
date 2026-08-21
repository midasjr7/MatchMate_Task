package com.example.matchmate.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.matchmate.data.local.MatchDatabase
import com.example.matchmate.data.local.MatchEntity
import com.example.matchmate.data.remote.RandomUserApi
import com.example.matchmate.security.DataCipher
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalPagingApi::class)
class MatchRemoteMediator(
    private val database: MatchDatabase,
    private val api: RandomUserApi,
    private val cipher: DataCipher,
) : RemoteMediator<Int, MatchEntity>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, MatchEntity>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> (database.matchDao().maxLoadedPage() ?: 0) + 1
        }
       // if (page > MAX_PAGE) return MediatorResult.Success(endOfPaginationReached = true)

        return try {
            val remote = api.getMatches(page, state.config.pageSize).results
            database.withTransaction {
                val dao = database.matchDao()
                val statuses = dao.getAll().associate { it.id to it.status }
                dao.upsertAll(remote.map { it.toEntity(statuses[it.login.uuid], cipher, page) })
            }
            MediatorResult.Success(endOfPaginationReached = remote.isEmpty())
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            MediatorResult.Error(error)
        }
    }

    private companion object {
        const val MAX_PAGE = 5
    }
}
