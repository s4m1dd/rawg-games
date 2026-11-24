package com.cancreative.rawggames.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RawgJsonTest {

    private val page = """
        {"count":2,"next":"https://api.rawg.io/api/games?page=2","results":[
          {"id":1,"name":"Game One","background_image":"https://img/1.jpg","rating":4.5,
           "released":"2020-05-01","genres":[{"id":4,"name":"Action"},{"id":3,"name":"Adventure"}]},
          {"id":2,"name":"Game Two","background_image":null,"rating":3.1,
           "released":null,"genres":[]}
        ]}
    """.trimIndent()

    @Test
    fun parsesGamesAndNextFlag() {
        val result = RawgJson.parsePage(page)

        assertEquals(2, result.games.size)
        assertTrue(result.hasNext)
        assertEquals("Game One", result.games[0].name)
        assertEquals(4.5, result.games[0].rating, 0.0)
        assertEquals(listOf("Action", "Adventure"), result.games[0].genres)
        assertNull(result.games[1].backgroundImage)
        assertNull(result.games[1].released)
        assertEquals(emptyList<String>(), result.games[1].genres)
    }

    @Test
    fun lastPageHasNoNext() {
        val result = RawgJson.parsePage("""{"results":[],"next":null}""")

        assertFalse(result.hasNext)
        assertEquals(0, result.games.size)
    }

    @Test
    fun parsesDetailWithDescriptionRaw() {
        val detail = RawgJson.parseDetail(
            """{"id":7,"name":"Hades","rating":4.3,"released":"2020-09-17",
                "genres":[{"name":"RPG"}],"description_raw":"<p>Desc</p>",
                "added":200,"platforms":[{"platform":{"name":"PlayStation 4"}},{"platform":{"name":"PC"}}]}"""
        )

        assertEquals(7, detail.id)
        assertEquals("Hades", detail.name)
        assertEquals(listOf("RPG"), detail.genres)
        assertEquals("<p>Desc</p>", detail.description)
        assertEquals(200, detail.added)
        assertEquals(listOf("PlayStation 4", "PC"), detail.platforms)
    }
}
