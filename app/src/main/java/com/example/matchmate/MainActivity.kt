package com.example.matchmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.matchmate.ui.matches.MatchListScreen
import com.example.matchmate.ui.matches.MatchesViewModel
import com.example.matchmate.ui.theme.MatchMateTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MatchesViewModel by viewModels {
        MatchesViewModel.factory((application as MatchMateApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MatchMateTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                MatchListScreen(
                    state = state,
                    onRefresh = viewModel::refresh,
                    onAccept = viewModel::accept,
                    onDecline = viewModel::decline,
                    onMessageShown = viewModel::consumeMessage,
                )
            }
        }
    }
}
