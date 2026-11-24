package com.cancreative.rawggames.data

import com.cancreative.rawggames.data.local.FavoriteDao
import com.cancreative.rawggames.data.local.FavoriteGame
import com.cancreative.rawggames.data.remote.GameDetail
import com.cancreative.rawggames.data.remote.GamePage
import com.cancreative.rawggames.data.remote.GamesApi
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val api: GamesApi,
    private val dao: FavoriteDao,
) {
    suspend fun games(page: Int, search: String?): GamePage = api.games(page, PAGE_SIZE, search)

    suspend fun detail(id: Int): GameDetail = api.detail(id)

    fun favorites(): Flow<List<FavoriteGame>> = dao.observeAll()

    fun favorite(id: Int): Flow<FavoriteGame?> = dao.observe(id)

    suspend fun toggleFavorite(detail: GameDetail) {
        if (dao.exists(detail.id)) {
            dao.delete(detail.id)
        } else {
            dao.insert(
                FavoriteGame(
                    id = detail.id,
                    name = detail.name,
                    backgroundImage = detail.backgroundImage,
                    rating = detail.rating,
                    released = detail.released,
                )
            )
        }
    }

    suspend fun addFavorite(game: FavoriteGame) = dao.insert(game)

    suspend fun removeFavorite(id: Int) = dao.delete(id)

    companion object {
        const val PAGE_SIZE = 20
    }
}
