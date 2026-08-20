package com.example.matchmate.data

import com.example.matchmate.data.local.MatchEntity
import com.example.matchmate.data.remote.DobDto
import com.example.matchmate.data.remote.LocationDto
import com.example.matchmate.data.remote.LoginDto
import com.example.matchmate.data.remote.NameDto
import com.example.matchmate.data.remote.PictureDto
import com.example.matchmate.data.remote.UserDto
import com.example.matchmate.domain.MatchStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class MatchMappingTest {
    private val user = UserDto(
        LoginDto("stable-id"), NameDto("Asha", "Shah"), DobDto(29),
        LocationDto("Pune", "Maharashtra", "India"), "asha@example.com",
        "1234567890", PictureDto("https://example.com/asha.jpg"),
    )

    @Test fun `remote profile defaults to pending`() {
        assertEquals(MatchStatus.PENDING.name, user.toEntity(null).status)
    }

    @Test fun `remote refresh preserves an accepted decision`() {
        assertEquals(MatchStatus.ACCEPTED.name, user.toEntity(MatchStatus.ACCEPTED.name).status)
    }

    @Test fun `invalid persisted status safely maps to pending`() {
        val entity = MatchEntity("id", "Name", 30, "City", "mail", "phone", "image", "UNKNOWN")
        assertEquals(MatchStatus.PENDING, entity.toDomain().status)
    }
}
