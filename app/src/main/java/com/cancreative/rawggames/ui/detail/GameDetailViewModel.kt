package com.cancreative.rawggames.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancreative.rawggames.ServiceLocator
import com.cancreative.rawggames.data.GameRepository
import com.cancreative.rawggames.data.remote.GameDetail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailState(
    val detail: GameDetail? = null,
    val isFavorite: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
)

class GameDetailViewModel : ViewModel() {

    private val repo: GameRepository get() = requireNotNull(ServiceLocator.repository)

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private var id = 0

    fun load(gameId: Int) {
        if (id == gameId) return
        id = gameId
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val detail = repo.detail(gameId)
                _state.update { it.copy(loading = false, detail = detail) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
        viewModelScope.launch {
            repo.favorite(gameId).collect { fav -> _state.update { it.copy(isFavorite = fav != null) } }
        }
    }

    fun toggleFavorite() {
        val detail = _state.value.detail ?: return
        viewModelScope.launch { repo.toggleFavorite(detail) }
    }
}
