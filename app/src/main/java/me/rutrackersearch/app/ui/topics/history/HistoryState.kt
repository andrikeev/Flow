package me.rutrackersearch.app.ui.topics.history

import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.Topic

sealed interface HistoryState {
    object Initial : HistoryState
    object Empty : HistoryState
    data class HistoryList(val items: List<TopicModel<Topic>>) : HistoryState
}
