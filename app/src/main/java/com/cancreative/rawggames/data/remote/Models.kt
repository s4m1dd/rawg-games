package com.cancreative.rawggames.data.remote

data class Game(
    val id: Int,
    val name: String,
    val backgroundImage: String?,
    val rating: Double,
    val released: String?,
    val genres: List<String>,
)

data class GameDetail(
    val id: Int,
    val name: String,
    val backgroundImage: String?,
    val rating: Double,
    val released: String?,
    val genres: List<String>,
    val description: String,
    val added: Int = 0,
    val platforms: List<String> = emptyList(),
    val developers: List<String> = emptyList(),
)

data class GamePage(
    val games: List<Game>,
    val hasNext: Boolean,
)
