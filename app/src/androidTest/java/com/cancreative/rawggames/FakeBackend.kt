package com.cancreative.rawggames

import com.cancreative.rawggames.data.local.FavoriteDao
import com.cancreative.rawggames.data.local.FavoriteGame
import com.cancreative.rawggames.data.remote.Game
import com.cancreative.rawggames.data.remote.GameDetail
import com.cancreative.rawggames.data.remote.GamePage
import com.cancreative.rawggames.data.remote.GamesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeApi : GamesApi {
    override suspend fun games(page: Int, pageSize: Int, search: String?): GamePage =
        GamePage(
            games = listOf(Game(1, "Hades", null, 4.3, "2020-09-17", listOf("RPG"))),
            hasNext = false,
        )

    override suspend fun detail(id: Int): GameDetail =
        GameDetail(id, "Hades", null, 4.3, "2020-09-17", listOf("RPG"), "Defy the god of the dead.")
}

class FakeDao : FavoriteDao {
    private val items = linkedMapOf<Int, FavoriteGame>()
    private val state = MutableStateFlow<List<FavoriteGame>>(emptyList())

    override fun observeAll(): Flow<List<FavoriteGame>> = state
    override fun observe(id: Int): Flow<FavoriteGame?> = state.map { list -> list.firstOrNull { it.id == id } }
    override suspend fun exists(id: Int) = items.containsKey(id)

    override suspend fun insert(game: FavoriteGame) {
        items[game.id] = game
        state.value = items.values.toList()
    }

    override suspend fun delete(id: Int) {
        items.remove(id)
        state.value = items.values.toList()
    }
}
