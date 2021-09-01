package me.rutrackersearch.app.ui.topics.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rutrackersearch.domain.usecase.ObserveFavoritesUseCase
import me.rutrackersearch.domain.usecase.RefreshFavoritesUseCase
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    refreshFavoritesUseCase: RefreshFavoritesUseCase,
) : ViewModel() {
    val state: StateFlow<FavoritesState> = observeFavoritesUseCase()
        .map { items ->
            if (items.isEmpty()) {
                FavoritesState.Empty
            } else {
                FavoritesState.FavoritesList(items)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, FavoritesState.Initial)

    init {
        viewModelScope.launch { refreshFavoritesUseCase() }
    }
}
