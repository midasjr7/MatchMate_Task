package com.example.matchmate.ui.matches

import androidx.paging.PagingData
import com.example.matchmate.data.MatchRepository
import com.example.matchmate.domain.Match
import com.example.matchmate.domain.MatchStatus
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MatchesViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `accept persists accepted decision and clears progress`() = runTest(dispatcher) {
        val repository = FakeMatchRepository()
        val viewModel = MatchesViewModel(repository)

        viewModel.accept("profile-1")
        advanceUntilIdle()

        assertEquals("profile-1" to MatchStatus.ACCEPTED, repository.lastUpdate)
        assertFalse("profile-1" in viewModel.uiState.value.updatingMatchIds)
        assertEquals(null, viewModel.uiState.value.message)
    }

    @Test fun `decline persists declined decision`() = runTest(dispatcher) {
        val repository = FakeMatchRepository()
        val viewModel = MatchesViewModel(repository)

        viewModel.decline("profile-2")
        advanceUntilIdle()

        assertEquals("profile-2" to MatchStatus.DECLINED, repository.lastUpdate)
    }

    @Test fun `failed decision exposes safe message and clears progress`() = runTest(dispatcher) {
        val repository = FakeMatchRepository(updateFailure = IOException("internal endpoint"))
        val viewModel = MatchesViewModel(repository)

        viewModel.accept("profile-3")
        advanceUntilIdle()

        assertEquals("You're offline. Showing saved matches.", viewModel.uiState.value.message)
        assertTrue(viewModel.uiState.value.updatingMatchIds.isEmpty())
    }

    @Test fun `message can be consumed`() = runTest(dispatcher) {
        val repository = FakeMatchRepository(updateFailure = IllegalStateException("private detail"))
        val viewModel = MatchesViewModel(repository)
        viewModel.accept("profile-4")
        advanceUntilIdle()

        viewModel.consumeMessage()

        assertEquals(null, viewModel.uiState.value.message)
    }
}

private class FakeMatchRepository(
    private val updateFailure: Exception? = null,
) : MatchRepository {
    var lastUpdate: Pair<String, MatchStatus>? = null

    override fun pagedMatches(): Flow<PagingData<Match>> = flowOf(PagingData.empty())
    override suspend fun refresh() = Unit

    override suspend fun updateStatus(id: String, status: MatchStatus) {
        updateFailure?.let { throw it }
        lastUpdate = id to status
    }
}
