package me.rutrackersearch.app.ui.topic.open

sealed interface OpenTopicState {
    object Loading : OpenTopicState
    data class Error(val error: Throwable) : OpenTopicState
}
