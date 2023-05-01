package flow.topic.topic

internal sealed interface TopicSideEffect {
    object Back: TopicSideEffect
    object OpenLogin: TopicSideEffect
    object ShowAddCommentDialog: TopicSideEffect
    object ShowLoginRequired: TopicSideEffect
    object ShowAddCommentError: TopicSideEffect
}