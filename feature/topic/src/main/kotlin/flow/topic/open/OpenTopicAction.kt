package flow.topic.open

sealed interface OpenTopicAction {
    object BackClick : OpenTopicAction
    object RetryClick : OpenTopicAction
}
