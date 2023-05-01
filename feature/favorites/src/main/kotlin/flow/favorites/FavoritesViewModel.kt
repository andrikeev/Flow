package flow.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.ObserveFavoritesUseCase
import flow.domain.usecase.RefreshFavoritesUseCase
import flow.logger.api.LoggerFactory
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
    private val refreshFavoritesUseCase: RefreshFavoritesUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<FavoritesState, FavoritesSideEffect> {
    private val logger = loggerFactory.get("FavoritesViewModel")

    override val container: Container<FavoritesState, FavoritesSideEffect> = container(
        initialState = FavoritesState.Initial,
        onCreate = { observeFavorites() },
    )

    fun perform(action: FavoritesAction) {
        when (action) {
            is FavoritesAction.TopicClick -> intent {
                postSideEffect(FavoritesSideEffect.OpenTopic(action.topicModel.topic.id))
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            logger.d { "Launch refresh favorites" }
            refreshFavoritesUseCase()
        }
        viewModelScope.launch {
            logger.d { "Start observing favorites" }
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
