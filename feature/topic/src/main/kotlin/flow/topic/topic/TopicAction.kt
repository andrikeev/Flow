package flow.topic.topic

internal sealed interface TopicAction {
    data class AddComment(val comment: String) : TopicAction
    object AddCommentClick : TopicAction
    object BackClick : TopicAction
    object FavoriteClick : TopicAction
    data class GoToPage(val page: Int) : TopicAction
    data class LastVisibleIndexChanged(val index: Int) : TopicAction
    object ListBottomReached : TopicAction
    object ListTopReached : TopicAction
    object LoginClick : TopicAction
    object RetryClick : TopicAction
}
