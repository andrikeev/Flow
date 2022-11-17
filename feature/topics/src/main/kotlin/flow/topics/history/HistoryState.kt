package flow.topics.history

import flow.models.topic.Topic
import flow.models.topic.TopicModel

sealed interface HistoryState {
    object Initial : HistoryState
    object Empty : HistoryState
    data class HistoryList(val items: List<TopicModel<Topic>>) : HistoryState
}
