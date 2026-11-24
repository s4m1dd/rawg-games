package com.cancreative.rawggames.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancreative.rawggames.ServiceLocator
import com.cancreative.rawggames.data.GameRepository
import com.cancreative.rawggames.data.local.FavoriteGame
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel() {

    private val repo: GameRepository get() = requireNotNull(ServiceLocator.repository)

    val favorites: StateFlow<List<FavoriteGame>> = repo.favorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun remove(id: Int) {
        viewModelScope.launch { repo.removeFavorite(id) }
    }
}
