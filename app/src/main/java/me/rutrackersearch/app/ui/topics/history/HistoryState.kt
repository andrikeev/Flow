package me.rutrackersearch.app.ui.topics.history

import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Topic

sealed interface HistoryState {
    object Initial : HistoryState
    object Empty : HistoryState
    data class HistoryList(val items: List<TopicModel<Topic>>) : HistoryState
}
