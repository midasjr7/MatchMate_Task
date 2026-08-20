package com.example.matchmate.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.matchmate.domain.Match
import com.example.matchmate.domain.MatchStatus

@Composable
fun MatchListScreen(
    state: MatchesUiState,
    onRefresh: () -> Unit,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onMessageShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let { snackbarHostState.showSnackbar(it) }
        if (state.message != null) onMessageShown()
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Header(state.isRefreshing, onRefresh)
            if (state.isOffline) OfflineBanner()
            when {
                state.isInitialLoading && state.matches.isEmpty() -> LoadingState()
                state.matches.isEmpty() -> EmptyState(onRefresh)
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(state.matches, key = Match::id) { match ->
                        MatchCard(
                            match = match,
                            isUpdating = match.id in state.updatingMatchIds,
                            onAccept = { onAccept(match.id) },
                            onDecline = { onDecline(match.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(isRefreshing: Boolean, onRefresh: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text("MatchMate", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("People who may be right for you", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        TextButton(onClick = onRefresh, enabled = !isRefreshing) {
            if (isRefreshing) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            else Text("Refresh")
        }
    }
    HorizontalDivider()
}

@Composable
private fun OfflineBanner() {
    Text(
        text = "Offline · Your saved matches are still available",
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        style = MaterialTheme.typography.labelLarge,
    )
}

@Composable
private fun MatchCard(match: Match, isUpdating: Boolean, onAccept: () -> Unit, onDecline: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column {
            Box {
                AsyncImage(
                    model = match.imageUrl,
                    contentDescription = "Profile photo of ${match.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(270.dp),
                )
                Text(
                    text = "${match.age} yrs",
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = .9f)).padding(horizontal = 12.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Column(Modifier.padding(18.dp)) {
                Text(match.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(match.location, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                Text(match.email, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
                Text(match.phone, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(18.dp))
                when (match.status) {
                    MatchStatus.PENDING -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onDecline, enabled = !isUpdating, modifier = Modifier.weight(1f)) { Text("Decline") }
                        Button(onClick = onAccept, enabled = !isUpdating, modifier = Modifier.weight(1f)) { Text("Accept") }
                    }
                    MatchStatus.ACCEPTED -> DecisionLabel("Member Accepted", true)
                    MatchStatus.DECLINED -> DecisionLabel("Member Declined", false)
                }
            }
        }
    }
}

@Composable
private fun DecisionLabel(text: String, accepted: Boolean) {
    val background = if (accepted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val foreground = if (accepted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(background).padding(14.dp)
            .semantics { contentDescription = text },
        color = foreground,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable private fun LoadingState() = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

@Composable
private fun EmptyState(onRefresh: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("No matches yet", style = MaterialTheme.typography.headlineSmall)
        Text("Connect to the internet and try again.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRefresh) { Text("Try again") }
    }
}
