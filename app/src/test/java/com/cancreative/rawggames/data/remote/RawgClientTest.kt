package com.cancreative.rawggames.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RawgClientTest {

    private val client = RawgClient("KEY")

    @Test
    fun buildsPagedUrl() {
        assertEquals(
            "https://api.rawg.io/api/games?page=2&page_size=20&key=KEY",
            client.buildUrl(2, 20, null),
        )
    }

    @Test
    fun encodesSearchQuery() {
        val url = client.buildUrl(1, 20, "god of war")

        assertTrue(url.contains("&search=god+of+war"))
    }

    @Test
    fun blankSearchIsOmitted() {
        assertFalse(client.buildUrl(1, 20, "  ").contains("search="))
    }

    private fun assertTrue(condition: Boolean) = org.junit.Assert.assertTrue(condition)
}
