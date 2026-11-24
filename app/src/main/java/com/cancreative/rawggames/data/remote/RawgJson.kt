package com.cancreative.rawggames.data.remote

import org.json.JSONObject

object RawgJson {

    fun parsePage(body: String): GamePage {
        val root = JSONObject(body)
        val results = root.optJSONArray("results")
        val games = buildList {
            if (results != null) for (i in 0 until results.length()) add(game(results.getJSONObject(i)))
        }
        return GamePage(games, hasNext = !root.isNull("next") && root.optString("next").isNotBlank())
    }

    fun parseDetail(body: String): GameDetail {
        val o = JSONObject(body)
        return GameDetail(
            id = o.getInt("id"),
            name = o.stringOrNull("name").orEmpty(),
            backgroundImage = o.stringOrNull("background_image"),
            rating = o.optDouble("rating", 0.0),
            released = o.stringOrNull("released"),
            genres = genres(o),
            description = o.stringOrNull("description_raw") ?: o.stringOrNull("description").orEmpty(),
            added = o.optInt("added", 0),
            platforms = platforms(o),
            developers = developers(o),
        )
    }

    fun game(o: JSONObject) = Game(
        id = o.getInt("id"),
        name = o.stringOrNull("name").orEmpty(),
        backgroundImage = o.stringOrNull("background_image"),
        rating = o.optDouble("rating", 0.0),
        released = o.stringOrNull("released"),
        genres = genres(o),
    )

    private fun platforms(o: JSONObject): List<String> {
        val arr = o.optJSONArray("platforms") ?: return emptyList()
        return (0 until arr.length()).mapNotNull {
            arr.optJSONObject(it)?.optJSONObject("platform")?.stringOrNull("name")
        }
    }

    private fun developers(o: JSONObject): List<String> {
        val arr = o.optJSONArray("developers") ?: return emptyList()
        return (0 until arr.length()).mapNotNull { arr.optJSONObject(it)?.stringOrNull("name") }
    }

    private fun genres(o: JSONObject): List<String> {
        val arr = o.optJSONArray("genres") ?: return emptyList()
        return (0 until arr.length()).mapNotNull { arr.optJSONObject(it)?.stringOrNull("name") }
    }

    // optString returns the literal "null" for JSON null.
    private fun JSONObject.stringOrNull(key: String): String? =
        if (isNull(key)) null else optString(key).ifBlank { null }
}
