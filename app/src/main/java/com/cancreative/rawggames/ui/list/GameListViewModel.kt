package com.cancreative.rawggames.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancreative.rawggames.ServiceLocator
import com.cancreative.rawggames.data.GameRepository
import com.cancreative.rawggames.data.remote.Game
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListState(
    val games: List<Game> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val hasNext: Boolean = true,
)

class GameListViewModel : ViewModel() {

    private val repo: GameRepository get() = requireNotNull(ServiceLocator.repository)

    private val _state = MutableStateFlow(ListState())
    val state: StateFlow<ListState> = _state.asStateFlow()

    private var page = 0
    private var query: String? = null
    private var job: Job? = null

    fun start() {
        if (page == 0 && !_state.value.loading) fetch(1)
    }

    fun search(raw: String?) {
        val clean = raw?.trim()?.takeIf { it.isNotEmpty() }
        if (clean == query && page > 0) return
        query = clean
        fetch(1)
    }

    fun loadMore() {
        val s = _state.value
        if (s.loading || !s.hasNext) return
        fetch(page + 1)
    }

    fun retry() = fetch(if (page > 0) page + 1 else 1)

    private fun fetch(target: Int) {
        job?.cancel()
        _state.update { it.copy(loading = true, error = null) }
        job = viewModelScope.launch {
            try {
                val data = repo.games(target, query)
                page = target
                _state.update {
                    it.copy(
                        loading = false,
                        games = if (target == 1) data.games else it.games + data.games,
                        hasNext = data.hasNext,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
}
