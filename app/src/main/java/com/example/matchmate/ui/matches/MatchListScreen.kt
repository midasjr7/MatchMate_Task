package com.example.matchmate.ui.matches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matchmate.domain.Match
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.matchmate.error.toUserMessage
import com.example.matchmate.ui.components.EmptyMatches
import com.example.matchmate.ui.components.InitialLoading
import com.example.matchmate.ui.components.LoadMoreError
import com.example.matchmate.ui.components.LoadingMore
import com.example.matchmate.ui.components.MatchCard
import com.example.matchmate.ui.components.MatchListHeader
import com.example.matchmate.ui.components.OfflineBanner

@Composable
fun MatchListScreen(
    state: MatchesUiState,
    matches: LazyPagingItems<Match>,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onMessageShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let { snackbarHostState.showSnackbar(it) }
        if (state.message != null) onMessageShown()
    }
    val pagingError = (matches.loadState.refresh as? LoadState.Error)?.error
        ?: (matches.loadState.append as? LoadState.Error)?.error
    LaunchedEffect(pagingError) {
        pagingError?.let { snackbarHostState.showSnackbar(it.toUserMessage()) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            val refreshState = matches.loadState.refresh
            MatchListHeader(refreshState is LoadState.Loading, matches::refresh)
            if (refreshState is LoadState.Error && matches.itemCount > 0) OfflineBanner()
            when {
                refreshState is LoadState.Loading && matches.itemCount == 0 -> InitialLoading()
                matches.itemCount == 0 -> EmptyMatches(matches::retry)
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(matches.itemCount, key = matches.itemKey(Match::id)) { index ->
                        matches[index]?.let { match ->
                            MatchCard(
                                match = match,
                                isUpdating = match.id in state.updatingMatchIds,
                                onAccept = { onAccept(match.id) },
                                onDecline = { onDecline(match.id) },
                            )
                        }
                    }
                    when (matches.loadState.append) {
                        is LoadState.Loading -> item { LoadingMore() }
                        is LoadState.Error -> item { LoadMoreError(matches::retry) }
                        else -> Unit
                    }
                }
            }
        }
    }
}
