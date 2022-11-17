package flow.topics.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.ObserveFavoritesUseCase
import flow.domain.usecase.RefreshFavoritesUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    refreshFavoritesUseCase: RefreshFavoritesUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
) : ViewModel(), ContainerHost<FavoritesState, FavoritesSideEffect> {
    override val container: Container<FavoritesState, FavoritesSideEffect> = container(
        initialState = FavoritesState.Initial,
        onCreate = {
            viewModelScope.launch { refreshFavoritesUseCase() }
            observeFavorites()
        },
    )

    fun perform(action: FavoritesAction) = intent {
        when (action) {
            is FavoritesAction.TopicClick -> postSideEffect(FavoritesSideEffect.OpenTopic(action.topics))
            is FavoritesAction.TorrentClick -> postSideEffect(FavoritesSideEffect.OpenTorrent(action.torrent))
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoritesUseCase()
                .catch { emit(emptyList()) }
                .map { items ->
                    if (items.isEmpty()) {
                        FavoritesState.Empty
                    } else {
                        FavoritesState.FavoritesList(items)
                    }
                }
                .collectLatest { state -> intent { reduce { state } } }
        }
    }
}
