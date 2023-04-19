package flow.visited

import flow.models.topic.Topic
import flow.models.topic.TopicModel

internal sealed interface VisitedAction {
    data class TopicClick(val topicModel: TopicModel<out Topic>) : VisitedAction
    data class FavoriteClick(val topicModel: TopicModel<out Topic>) : VisitedAction
}
