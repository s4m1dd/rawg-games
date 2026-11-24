package com.cancreative.rawggames.data

import com.cancreative.rawggames.data.local.FavoriteDao
import com.cancreative.rawggames.data.local.FavoriteGame
import com.cancreative.rawggames.data.remote.GameDetail
import com.cancreative.rawggames.data.remote.GamePage
import com.cancreative.rawggames.data.remote.GamesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRepositoryTest {

    private val detail = GameDetail(
        id = 1,
        name = "Hades",
        backgroundImage = null,
        rating = 4.3,
        released = "2020-09-17",
        genres = listOf("RPG"),
        description = "desc",
    )

    private fun repo(dao: FakeDao) = GameRepository(NoNetwork, dao)

    @Test
    fun toggleAddsThenRemoves() = runTest {
        val dao = FakeDao()
        val repo = repo(dao)

        repo.toggleFavorite(detail)
        assertTrue(dao.exists(1))

        repo.toggleFavorite(detail)
        assertFalse(dao.exists(1))
    }

    @Test
    fun favoriteFlowReflectsStoredRow() = runTest {
        val dao = FakeDao()
        val repo = repo(dao)

        repo.addFavorite(FavoriteGame(1, "Hades", null, 4.3, "2020-09-17"))
        assertEquals("Hades", repo.favorite(1).first()?.name)

        repo.removeFavorite(1)
        assertEquals(null, repo.favorite(1).first())
    }

    private object NoNetwork : GamesApi {
        override suspend fun games(page: Int, pageSize: Int, search: String?): GamePage =
            error("not used")
        override suspend fun detail(id: Int): GameDetail = error("not used")
    }

    private class FakeDao : FavoriteDao {
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
}
