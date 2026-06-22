package flow.visited

import androidx.lifecycle.ViewModel
import flow.common.runSuspendCatching
import flow.domain.usecase.ObserveVisitedUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
import flow.logger.api.LoggerFactory
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

internal class VisitedViewModel(
    private val observeVisitedUseCase: ObserveVisitedUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<VisitedState, VisitedSideEffect> {
    private val logger = loggerFactory.get("VisitedViewModel")

    override val container: Container<VisitedState, VisitedSideEffect> = container(
        initialState = VisitedState.Initial,
        onCreate = {
            repeatOnSubscription {
                logger.d { "Start observing visited" }
                observeVisitedUseCase().collectLatest { items ->
                    logger.d { "On new visited list: $items" }
                    reduce {
                        if (items.isEmpty()) {
                            VisitedState.Empty
                        } else {
                            VisitedState.VisitedList(items)
                        }
                    }
                }
            }
        },
    )

    fun perform(action: VisitedAction) {
        logger.d { "Perform $action" }
        when (action) {
            is VisitedAction.FavoriteClick -> onFavoriteClick(action.topicModel)
            is VisitedAction.TopicClick -> onTopicClick(action.topicModel)
        }
    }

    private fun onFavoriteClick(topicModel: TopicModel<out Topic>) = intent {
        runSuspendCatching { toggleFavoriteUseCase(topicModel.topic.id) }
            .onFailure { postSideEffect(VisitedSideEffect.ShowFavoriteToggleError) }
    }

    private fun onTopicClick(topicModel: TopicModel<out Topic>) = intent {
        postSideEffect(VisitedSideEffect.OpenTopic(topicModel.topic.id))
    }
}
