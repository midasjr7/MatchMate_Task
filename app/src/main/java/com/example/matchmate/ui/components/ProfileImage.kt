package com.example.matchmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Man
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Woman
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.matchmate.domain.Gender

@Composable
fun ProfileImage(
    imageUrl: String,
    profileName: String,
    gender: Gender,
    modifier: Modifier = Modifier,
) {
    var retryAttempt by remember(imageUrl) { mutableIntStateOf(0) }
    val context = LocalContext.current
    val request = remember(imageUrl, retryAttempt) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .memoryCacheKey("$imageUrl:attempt:$retryAttempt")
            .diskCacheKey(imageUrl)
            .build()
    }
    SubcomposeAsyncImage(
        model = request,
        contentDescription = "Profile photo of $profileName",
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxWidth().height(270.dp),
        loading = { PersonPlaceholder(gender = gender, showProgress = true) },
        error = {
            PersonPlaceholder(
                gender = gender,
                action = {
                    Button(onClick = { retryAttempt++ }) { Text("Retry photo") }
                },
            )
        },
        success = { SubcomposeAsyncImageContent() },
    )
}

@Composable
private fun PersonPlaceholder(
    gender: Gender,
    showProgress: Boolean = false,
    action: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = when (gender) {
                    Gender.MALE -> Icons.Rounded.Man
                    Gender.FEMALE -> Icons.Rounded.Woman
                    Gender.UNKNOWN -> Icons.Rounded.Person
                },
                contentDescription = when (gender) {
                    Gender.MALE -> "Male profile placeholder"
                    Gender.FEMALE -> "Female profile placeholder"
                    Gender.UNKNOWN -> "Profile placeholder"
                },
                modifier = Modifier.size(104.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (showProgress) CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 3.dp)
            action?.invoke()
        }
    }
}
