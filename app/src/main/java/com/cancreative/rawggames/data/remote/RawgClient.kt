package com.cancreative.rawggames.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

interface GamesApi {
    suspend fun games(page: Int, pageSize: Int, search: String?): GamePage
    suspend fun detail(id: Int): GameDetail
}

class RawgClient(private val apiKey: String) : GamesApi {

    override suspend fun games(page: Int, pageSize: Int, search: String?): GamePage =
        withContext(Dispatchers.IO) { RawgJson.parsePage(get(buildUrl(page, pageSize, search))) }

    override suspend fun detail(id: Int): GameDetail =
        withContext(Dispatchers.IO) { RawgJson.parseDetail(get("$BASE/api/games/$id?key=$apiKey")) }

    fun buildUrl(page: Int, pageSize: Int, search: String?): String {
        val url = StringBuilder("$BASE/api/games?page=$page&page_size=$pageSize&key=$apiKey")
        if (!search.isNullOrBlank()) {
            url.append("&search=").append(URLEncoder.encode(search, "UTF-8"))
        }
        return url.toString()
    }

    private fun get(url: String): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        return try {
            conn.connectTimeout = 15_000
            conn.readTimeout = 15_000
            val code = conn.responseCode
            if (code !in 200..299) throw IllegalStateException("RAWG API HTTP $code")
            conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        const val BASE = "https://api.rawg.io"
    }
}
