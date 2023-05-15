package flow.visited

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.ObserveVisitedUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
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
internal class VisitedViewModel @Inject constructor(
    private val observeVisitedUseCase: ObserveVisitedUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<VisitedState, VisitedSideEffect> {
    private val logger = loggerFactory.get("VisitedViewModel")

    override val container: Container<VisitedState, VisitedSideEffect> = container(
        initialState = VisitedState.Initial,
        onCreate = { observeVisited() },
    )

    fun perform(action: VisitedAction) {
        logger.d { "Perform $action" }
        when (action) {
            is VisitedAction.FavoriteClick -> onFavoriteClick(action.topicModel)
            is VisitedAction.TopicClick -> onTopicClick(action.topicModel)
        }
    }

    private fun observeVisited() = intent {
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

    private fun onFavoriteClick(topicModel: TopicModel<out Topic>) = intent {
        toggleFavoriteUseCase(topicModel.topic.id)
    }

    private fun onTopicClick(topicModel: TopicModel<out Topic>) = intent {
        postSideEffect(VisitedSideEffect.OpenTopic(topicModel.topic.id))
    }
}
