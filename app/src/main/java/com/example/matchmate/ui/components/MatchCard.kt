package com.example.matchmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.matchmate.domain.Match
import com.example.matchmate.domain.MatchStatus

@Composable
fun MatchCard(
    match: Match,
    isUpdating: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column {
            Box {
                ProfileImage(
                    imageUrl = match.imageUrl,
                    profileName = match.name,
                    gender = match.gender,
                )
                Text(
                    text = "${match.age} yrs",
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = .9f))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Column(Modifier.padding(18.dp)) {
                Text(match.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    match.location,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(10.dp))
                Text(match.email, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
                Text(match.phone, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(18.dp))
                when (match.status) {
                    MatchStatus.PENDING -> MatchActions(isUpdating, onAccept, onDecline)
                    MatchStatus.ACCEPTED -> DecisionLabel("Member Accepted", accepted = true)
                    MatchStatus.DECLINED -> DecisionLabel("Member Declined", accepted = false)
                }
            }
        }
    }
}

@Composable
private fun MatchActions(isUpdating: Boolean, onAccept: () -> Unit, onDecline: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onDecline, enabled = !isUpdating, modifier = Modifier.weight(1f)) {
            Text("Decline")
        }
        Button(onClick = onAccept, enabled = !isUpdating, modifier = Modifier.weight(1f)) {
            Text("Accept")
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
