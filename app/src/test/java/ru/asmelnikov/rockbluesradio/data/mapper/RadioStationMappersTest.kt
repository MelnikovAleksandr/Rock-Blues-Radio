package ru.asmelnikov.rockbluesradio.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.asmelnikov.rockbluesradio.data.local.asset.RadioStationDto
import ru.asmelnikov.rockbluesradio.data.local.database.FavoriteStationEntity
import ru.asmelnikov.rockbluesradio.testing.testStation

class RadioStationMappersTest {

    private fun dto(name: String? = "Rock FM", tags: String? = "rock") = RadioStationDto(
        stationUuid = "uuid",
        name = name,
        urlResolved = "https://stream.example/rock",
        favicon = "https://favicon.example/rock.png",
        tags = tags,
    )

    @Test
    fun `dto maps all fields and is not favorite`() {
        val station = dto().toDomain()

        assertEquals("uuid", station.id)
        assertEquals("Rock FM", station.name)
        assertEquals("https://stream.example/rock", station.streamUrl)
        assertEquals("https://favicon.example/rock.png", station.faviconUrl)
        assertEquals(listOf("Rock"), station.genres)
        assertFalse(station.isFavorite)
    }

    @Test
    fun `missing optional fields become empty values`() {
        val station = RadioStationDto(stationUuid = "uuid").toDomain()

        assertEquals("", station.name)
        assertEquals("", station.streamUrl)
        assertEquals("", station.faviconUrl)
        assertTrue(station.genres.isEmpty())
    }

    @Test
    fun `leading non alphanumeric characters are removed from name`() {
        assertEquals("Rock FM", dto(name = " ..::Rock FM").toDomain().name)
        assertEquals("", dto(name = "!!!").toDomain().name)
        assertEquals("1.FM Blues", dto(name = "1.FM Blues").toDomain().name)
    }

    @Test
    fun `genres are capitalized`() {
        assertEquals(listOf("Rock", "Blues"), dto(tags = "rock,blues").toDomain().genres)
    }

    @Test
    fun `genres longer than ten characters are skipped`() {
        assertEquals(listOf("Rock"), dto(tags = "progressive rock,rock").toDomain().genres)
    }

    @Test
    fun `at most four genres are kept`() {
        assertEquals(listOf("A", "B", "C", "D"), dto(tags = "a,b,c,d,e,f").toDomain().genres)
    }

    @Test
    fun `blank tags produce no genres`() {
        assertTrue(dto(tags = "").toDomain().genres.isEmpty())
        assertEquals(listOf("Rock", "Blues"), dto(tags = "rock, ,blues,").toDomain().genres)
    }

    @Test
    fun `entity maps to favorite station`() {
        val entity = FavoriteStationEntity(
            favicon = "icon",
            name = "Blues FM",
            stationUuid = "uuid",
            tags = "Blues,Soul",
            urlResolved = "url",
        )

        val station = entity.toDomain()

        assertEquals(testStation("uuid", "Blues FM", "url", "icon", true, listOf("Blues", "Soul")), station)
    }

    @Test
    fun `station survives a round trip through the entity`() {
        val station = testStation("uuid", genres = listOf("Rock", "Blues"), isFavorite = true)

        assertEquals(station, station.toFavoriteEntity().toDomain())
    }

    @Test
    fun `station without genres is stored with empty tags and read back without genres`() {
        val entity = testStation("uuid", genres = emptyList()).toFavoriteEntity()

        assertEquals("", entity.tags)
        assertTrue(entity.toDomain().genres.isEmpty())
    }
}
