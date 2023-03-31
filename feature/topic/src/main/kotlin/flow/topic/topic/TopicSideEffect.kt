package flow.topic.topic

sealed interface TopicSideEffect {
    object Back: TopicSideEffect
    object OpenLogin: TopicSideEffect
    object ShowAddCommentDialog: TopicSideEffect
    object ShowLoginRequired: TopicSideEffect
    object ShowAddCommentError: TopicSideEffect
}