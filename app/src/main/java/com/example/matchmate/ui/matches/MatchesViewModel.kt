package com.example.matchmate.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.matchmate.data.MatchRepository
import com.example.matchmate.domain.Match
import com.example.matchmate.domain.MatchStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.matchmate.error.toUserMessage
import com.example.matchmate.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.supervisorScope

data class MatchesUiState(
    val updatingMatchIds: Set<String> = emptySet(),
    val message: String? = null,
)

@HiltViewModel
class MatchesViewModel @Inject constructor(private val repository: MatchRepository) : ViewModel() {
    private val operationErrorHandler = CoroutineExceptionHandler { _, error ->
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Unhandled match operation failure", error)
        } else {
            Log.e(TAG, "Unhandled match operation failure")
        }
        _uiState.update { it.copy(message = "Something went wrong. Please try again.") }
    }

    val matches: kotlinx.coroutines.flow.Flow<PagingData<Match>> =
        repository.pagedMatches().cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(MatchesUiState())
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    fun accept(id: String) = updateDecision(id, MatchStatus.ACCEPTED)
    fun decline(id: String) = updateDecision(id, MatchStatus.DECLINED)

    private fun updateDecision(id: String, status: MatchStatus) {
        if (id in _uiState.value.updatingMatchIds) return
        viewModelScope.launch(operationErrorHandler) {
            supervisorScope {
                launch {
                    _uiState.update { it.copy(updatingMatchIds = it.updatingMatchIds + id) }
                    try {
                        repository.updateStatus(id, status)
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Exception) {
                        _uiState.update { it.copy(message = error.toUserMessage()) }
                    } finally {
                        _uiState.update { it.copy(updatingMatchIds = it.updatingMatchIds - id) }
                    }
                }
            }
        }
    }

    fun consumeMessage() = _uiState.update { it.copy(message = null) }

    private companion object {
        const val TAG = "MatchMate"
    }
}
