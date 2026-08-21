package com.example.matchmate

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.matchmate.ui.matches.MatchListScreen
import com.example.matchmate.ui.matches.MatchesViewModel
import com.example.matchmate.ui.theme.MatchMateTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.matchmate.worker.ProfileRefreshWorker
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.supervisorScope

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MatchesViewModel by viewModels()
    private val startupErrorHandler = CoroutineExceptionHandler { _, error ->
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Activity startup task failed", error)
        } else {
            Log.e(TAG, "Activity startup task failed")
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(startupErrorHandler) {
            supervisorScope {
                launch { ProfileRefreshWorker.schedule(this@MainActivity) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MatchMateTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val matches = viewModel.matches.collectAsLazyPagingItems()
                MatchListScreen(
                    state = state,
                    matches = matches,
                    onAccept = viewModel::accept,
                    onDecline = viewModel::decline,
                    onMessageShown = viewModel::consumeMessage,
                )
            }
        }
    }

    private companion object {
        const val TAG = "MatchMate"
    }
}
