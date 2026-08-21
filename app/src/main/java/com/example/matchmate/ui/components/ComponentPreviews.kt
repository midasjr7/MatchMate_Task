package com.example.matchmate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matchmate.domain.Match
import com.example.matchmate.domain.MatchStatus
import com.example.matchmate.ui.theme.MatchMateTheme

@Preview(name = "Pending match", showBackground = true, widthDp = 390)
@Preview(name = "Pending match - dark", showBackground = true, widthDp = 390, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PendingMatchCardPreview() {
    PreviewSurface {
        MatchCard(
            match = sampleMatch(MatchStatus.PENDING),
            isUpdating = false,
            onAccept = {},
            onDecline = {},
        )
    }
}

@Preview(name = "Accepted match", showBackground = true, widthDp = 390)
@Composable
private fun AcceptedMatchCardPreview() {
    PreviewSurface {
        MatchCard(
            match = sampleMatch(MatchStatus.ACCEPTED),
            isUpdating = false,
            onAccept = {},
            onDecline = {},
        )
    }
}

@Preview(name = "Declined match", showBackground = true, widthDp = 390)
@Composable
private fun DeclinedMatchCardPreview() {
    PreviewSurface {
        MatchCard(
            match = sampleMatch(MatchStatus.DECLINED),
            isUpdating = false,
            onAccept = {},
            onDecline = {},
        )
    }
}

@Preview(name = "List chrome", showBackground = true, widthDp = 390)
@Composable
private fun MatchListChromePreview() {
    MatchMateTheme {
        Surface {
            Column {
                MatchListHeader(isRefreshing = false, onRefresh = {})
                OfflineBanner()
            }
        }
    }
}

@Preview(name = "Empty state", showBackground = true, widthDp = 390, heightDp = 600)
@Composable
private fun EmptyMatchesPreview() {
    MatchMateTheme {
        Surface {
            EmptyMatches(onRetry = {})
        }
    }
}

@Composable
private fun PreviewSurface(content: @Composable () -> Unit) {
    MatchMateTheme {
        Surface {
            Column(Modifier.padding(16.dp)) { content() }
        }
    }
}

private fun sampleMatch(status: MatchStatus) = Match(
    id = "preview-profile",
    name = "Aarav Sharma",
    age = 29,
    location = "Bengaluru, Karnataka, India",
    email = "aarav.sharma@example.com",
    phone = "+91 98765 43210",
    imageUrl = "https://randomuser.me/api/portraits/men/32.jpg",
    status = status,
)
