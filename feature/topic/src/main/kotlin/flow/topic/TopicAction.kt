package flow.topic

import flow.models.forum.Category
import flow.models.topic.Author

internal sealed interface TopicAction {
    data class AddComment(val comment: String) : TopicAction
    object AddCommentClick : TopicAction
    data class AuthorClick(val author: Author) : TopicAction
    object BackClick : TopicAction
    data class CategoryClick(val category: Category) : TopicAction
    object FavoriteClick : TopicAction
    data class GoToPage(val page: Int) : TopicAction
    data class LastVisibleIndexChanged(val index: Int) : TopicAction
    object ListBottomReached : TopicAction
    object ListTopReached : TopicAction
    object LoginClick : TopicAction
    data class MagnetClick(val link: String) : TopicAction
    data class OpenFileClick(val uri: String) : TopicAction
    object RetryClick : TopicAction
    object ShareClick : TopicAction
    data class TorrentFileClick(val title: String) : TopicAction
}
