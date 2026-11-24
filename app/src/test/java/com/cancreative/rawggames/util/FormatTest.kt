package com.cancreative.rawggames.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatTest {

    @Test
    fun formatsIsoDate() {
        assertEquals("24 Nov 2017", formatReleaseDate("2017-11-24"))
    }

    @Test
    fun blanksNullAndKeepsGarbage() {
        assertEquals("", formatReleaseDate(null))
        assertEquals("TBA", formatReleaseDate("TBA"))
    }
}
