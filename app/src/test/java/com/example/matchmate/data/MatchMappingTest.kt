package com.example.matchmate.data

import com.example.matchmate.data.local.MatchEntity
import com.example.matchmate.data.remote.DobDto
import com.example.matchmate.data.remote.LocationDto
import com.example.matchmate.data.remote.LoginDto
import com.example.matchmate.data.remote.NameDto
import com.example.matchmate.data.remote.PictureDto
import com.example.matchmate.data.remote.UserDto
import com.example.matchmate.domain.MatchStatus
import com.example.matchmate.security.DataCipher
import org.junit.Assert.assertEquals
import org.junit.Test

class MatchMappingTest {
    private val cipher = object : DataCipher {
        override fun encrypt(plainText: String) = "encrypted:$plainText"
        override fun decrypt(storedValue: String) = storedValue.removePrefix("encrypted:")
    }
    private val user = UserDto(
        "female", LoginDto("stable-id"), NameDto("Asha", "Shah"), DobDto(29),
        LocationDto("Pune", "Maharashtra", "India"), "asha@example.com",
        "1234567890", PictureDto("https://example.com/asha.jpg"),
    )

    @Test fun `remote profile defaults to pending`() {
        assertEquals(MatchStatus.PENDING.name, user.toEntity(null, cipher).status)
    }

    @Test fun `remote refresh preserves an accepted decision`() {
        assertEquals(MatchStatus.ACCEPTED.name, user.toEntity(MatchStatus.ACCEPTED.name, cipher).status)
    }

    @Test fun `invalid persisted status safely maps to pending`() {
        val entity = MatchEntity("id", "Name", 30, "City", "mail", "phone", "image", "UNKNOWN")
        assertEquals(MatchStatus.PENDING, entity.toDomain(cipher).status)
    }

    @Test fun `personally identifiable fields are encrypted before persistence`() {
        val entity = user.toEntity(null, cipher)
        assertEquals("encrypted:Asha Shah", entity.name)
        assertEquals("encrypted:asha@example.com", entity.email)
        assertEquals("Asha Shah", entity.toDomain(cipher).name)
    }
}
