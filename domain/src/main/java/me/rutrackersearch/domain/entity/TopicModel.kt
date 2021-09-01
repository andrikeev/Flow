package me.rutrackersearch.domain.entity

import me.rutrackersearch.domain.entity.topic.Topic

data class TopicModel<T : Topic>(
    val data: T,
    val isVisited: Boolean = false,
    val isFavorite: Boolean = false,
    val isNew: Boolean = false,
    val hasUpdate: Boolean = false,
)
