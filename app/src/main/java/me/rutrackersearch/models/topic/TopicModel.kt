package me.rutrackersearch.models.topic

data class TopicModel<T : Topic>(
    val topic: T,
    val isVisited: Boolean = false,
    val isFavorite: Boolean = false,
    val isNew: Boolean = false,
    val hasUpdate: Boolean = false,
)
