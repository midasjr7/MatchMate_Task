package com.example.matchmate.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchmate.data.MatchRepository
import com.example.matchmate.domain.Match
import com.example.matchmate.domain.MatchStatus
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MatchesUiState(
    val matches: List<Match> = emptyList(),
    val isInitialLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val updatingMatchIds: Set<String> = emptySet(),
    val message: String? = null,
)

class MatchesViewModel(private val repository: MatchRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MatchesUiState())
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeMatches().collect { matches ->
                _uiState.update { it.copy(matches = matches, isInitialLoading = false) }
            }
        }
        refresh()
    }

    fun refresh() {
        if (_uiState.value.isRefreshing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, message = null) }
            runCatching { repository.refresh() }
                .onSuccess { _uiState.update { it.copy(isRefreshing = false, isOffline = false) } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            isInitialLoading = false,
                            isOffline = error is IOException,
                            message = if (error is IOException) {
                                "You're offline. Showing saved matches."
                            } else {
                                "Couldn't refresh matches. Please try again."
                            },
                        )
                    }
                }
        }
    }

    fun accept(id: String) = updateDecision(id, MatchStatus.ACCEPTED)
    fun decline(id: String) = updateDecision(id, MatchStatus.DECLINED)

    private fun updateDecision(id: String, status: MatchStatus) {
        if (id in _uiState.value.updatingMatchIds) return
        viewModelScope.launch {
            _uiState.update { it.copy(updatingMatchIds = it.updatingMatchIds + id) }
            runCatching { repository.updateStatus(id, status) }
                .onFailure { _uiState.update { it.copy(message = "Couldn't save your decision.") } }
            _uiState.update { it.copy(updatingMatchIds = it.updatingMatchIds - id) }
        }
    }

    fun consumeMessage() = _uiState.update { it.copy(message = null) }

    companion object {
        fun factory(repository: MatchRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    MatchesViewModel(repository) as T
            }
    }
}
