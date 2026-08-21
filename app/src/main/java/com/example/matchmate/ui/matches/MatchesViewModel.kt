package com.example.matchmate.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class MatchesUiState(
    val updatingMatchIds: Set<String> = emptySet(),
    val message: String? = null,
)

@HiltViewModel
class MatchesViewModel @Inject constructor(private val repository: MatchRepository) : ViewModel() {
    val matches: kotlinx.coroutines.flow.Flow<PagingData<Match>> =
        repository.pagedMatches().cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(MatchesUiState())
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

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

}
