package flow.favorites

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.ObserveFavoritesUseCase
import flow.logger.api.LoggerFactory
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<FavoritesState, FavoritesSideEffect> {
    private val logger = loggerFactory.get("FavoritesViewModel")

    override val container: Container<FavoritesState, FavoritesSideEffect> = container(
        initialState = FavoritesState.Initial,
        onCreate = { observeFavorites() },
    )

    fun perform(action: FavoritesAction) {
        logger.d { "Perform $action" }
        when (action) {
            is FavoritesAction.TopicClick -> onTopicClick(action.topicModel)
        }
    }

    private fun observeFavorites() = intent {
        logger.d { "Start observing favorites" }
        observeFavoritesUseCase().collectLatest { items ->
            logger.d { "On new favorites list: $items" }
            reduce {
                if (items.isEmpty()) {
                    FavoritesState.Empty
                } else {
                    FavoritesState.FavoritesList(items)
                }
            }
        }
    }

    private fun onTopicClick(topicModel: TopicModel<out Topic>) = intent {
        postSideEffect(FavoritesSideEffect.OpenTopic(topicModel.topic.id))
    }
}
