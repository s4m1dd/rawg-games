package com.cancreative.rawggames.data.remote

import org.json.JSONObject

object RawgJson {

    fun parsePage(body: String): GamePage {
        val root = JSONObject(body)
        val results = root.optJSONArray("results")
        val games = buildList {
            if (results != null) for (i in 0 until results.length()) add(game(results.getJSONObject(i)))
        }
        // optString returns the literal "null" for JSON null, so check isNull first.
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

    private fun genres(o: JSONObject): List<String> {
        val arr = o.optJSONArray("genres") ?: return emptyList()
        return (0 until arr.length()).mapNotNull { arr.optJSONObject(it)?.stringOrNull("name") }
    }

    // org.json returns the string "null" for JSON null values via optString.
    private fun JSONObject.stringOrNull(key: String): String? =
        if (isNull(key)) null else optString(key).ifBlank { null }
}
