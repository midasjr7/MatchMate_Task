package com.example.matchmate.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.matchmate.data.local.MatchDatabase
import com.example.matchmate.data.local.MatchEntity
import com.example.matchmate.data.remote.DobDto
import com.example.matchmate.data.remote.LocationDto
import com.example.matchmate.data.remote.LoginDto
import com.example.matchmate.data.remote.NameDto
import com.example.matchmate.data.remote.PictureDto
import com.example.matchmate.data.remote.RandomUserApi
import com.example.matchmate.data.remote.RandomUserResponse
import com.example.matchmate.data.remote.UserDto
import com.example.matchmate.domain.MatchStatus
import com.example.matchmate.security.DataCipher
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class MatchRemoteMediatorTest {
    private lateinit var database: MatchDatabase
    private val cipher = object : DataCipher {
        override fun encrypt(plainText: String) = "encrypted:$plainText"
        override fun decrypt(storedValue: String) = storedValue.removePrefix("encrypted:")
    }

    @Before fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MatchDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After fun closeDatabase() = database.close()

    @Test fun refreshStoresFirstPageInRoom() = runTest {
        val mediator = MatchRemoteMediator(database, FakeApi(), cipher)

        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals(2, database.matchDao().getAll().size)
        assertEquals(1, database.matchDao().maxLoadedPage())
    }

    @Test fun appendLoadsAndStoresNextPage() = runTest {
        val mediator = MatchRemoteMediator(database, FakeApi(), cipher)
        mediator.load(LoadType.REFRESH, pagingState())

        val result = mediator.load(LoadType.APPEND, pagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals(4, database.matchDao().getAll().size)
        assertEquals(2, database.matchDao().maxLoadedPage())
    }

    @Test fun refreshPreservesExistingDecision() = runTest {
        val mediator = MatchRemoteMediator(database, FakeApi(), cipher)
        mediator.load(LoadType.REFRESH, pagingState())
        database.matchDao().updateStatus("profile-1-0", MatchStatus.ACCEPTED.name)

        mediator.load(LoadType.REFRESH, pagingState())

        val profile = database.matchDao().getAll().first { it.id == "profile-1-0" }
        assertEquals(MatchStatus.ACCEPTED.name, profile.status)
    }

    @Test fun networkFailureReturnsPagingErrorWithoutDeletingCache() = runTest {
        val successfulMediator = MatchRemoteMediator(database, FakeApi(), cipher)
        successfulMediator.load(LoadType.REFRESH, pagingState())
        val failingMediator = MatchRemoteMediator(database, FakeApi(IOException("offline")), cipher)

        val result = failingMediator.load(LoadType.APPEND, pagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertEquals(2, database.matchDao().getAll().size)
    }

    private fun pagingState() = PagingState<Int, MatchEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = 2, enablePlaceholders = false),
        leadingPlaceholderCount = 0,
    )
}

private class FakeApi(private val failure: IOException? = null) : RandomUserApi {
    override suspend fun getMatches(page: Int, pageSize: Int): RandomUserResponse {
        failure?.let { throw it }
        return RandomUserResponse(
            List(pageSize) { index ->
                UserDto(
                    gender = if (index % 2 == 0) "female" else "male",
                    login = LoginDto("profile-$page-$index"),
                    name = NameDto("First$page", "User$index"),
                    dob = DobDto(25 + index),
                    location = LocationDto("City", "State", "Country"),
                    email = "user$index@example.com",
                    phone = "12345$index",
                    picture = PictureDto("https://example.com/$page/$index.jpg"),
                )
            },
        )
    }
}
