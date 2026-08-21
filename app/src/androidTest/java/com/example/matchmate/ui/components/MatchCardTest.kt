package com.example.matchmate.ui.components

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import com.example.matchmate.domain.Gender
import com.example.matchmate.domain.Match
import com.example.matchmate.domain.MatchStatus
import com.example.matchmate.ui.theme.MatchMateTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MatchCardTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun pendingCardInvokesBothDecisionActions() {
        var accepted = false
        var declined = false
        composeRule.setContent {
            MatchMateTheme {
                MatchCard(sampleMatch(), false, { accepted = true }, { declined = true })
            }
        }

        composeRule.onNodeWithText("Accept").performClick()
        composeRule.onNodeWithText("Decline").performClick()

        assertTrue(accepted)
        assertTrue(declined)
    }

    @Test fun acceptedCardShowsStatusAndHidesActions() {
        composeRule.setContent {
            MatchMateTheme { MatchCard(sampleMatch(MatchStatus.ACCEPTED), false, {}, {}) }
        }

        composeRule.onNodeWithText("Member Accepted").assertIsDisplayed()
        composeRule.onAllNodesWithText("Accept").assertCountEquals(0)
        composeRule.onAllNodesWithText("Decline").assertCountEquals(0)
    }

    @Test fun failedFemaleImageShowsGenderPlaceholderAndRetry() {
        composeRule.setContent {
            MatchMateTheme {
                ProfileImage(
                    imageUrl = "file:///missing/profile.jpg",
                    profileName = "Asha Shah",
                    gender = Gender.FEMALE,
                )
            }
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeRule.onNodeWithText("Retry photo").assertIsDisplayed()
            }.isSuccess
        }
        composeRule.onNodeWithContentDescription("Female profile placeholder").assertIsDisplayed()
        composeRule.onNodeWithText("Retry photo").performClick()
    }
}

private fun sampleMatch(status: MatchStatus = MatchStatus.PENDING) = Match(
    id = "profile-1",
    name = "Asha Shah",
    age = 29,
    location = "Pune, Maharashtra, India",
    email = "asha@example.com",
    phone = "1234567890",
    imageUrl = "file:///missing/profile.jpg",
    gender = Gender.FEMALE,
    status = status,
)
