package me.rutrackersearch.app.ui.topic.open

sealed interface OpenTopicAction {
    object BackClick : OpenTopicAction
    object RetryClick : OpenTopicAction
}
