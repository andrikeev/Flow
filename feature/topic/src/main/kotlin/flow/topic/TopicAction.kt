package flow.topic

import flow.models.forum.Category
import flow.models.topic.Author

internal sealed interface TopicAction {
    data class AddComment(val comment: String) : TopicAction
    data object AddCommentClick : TopicAction
    data class AuthorClick(val author: Author) : TopicAction
    data object BackClick : TopicAction
    data class CategoryClick(val category: Category) : TopicAction
    data object FavoriteClick : TopicAction
    data class GoToPage(val page: Int) : TopicAction
    data object LoginClick : TopicAction
    data class MagnetClick(val link: String) : TopicAction
    data class OpenFileClick(val uri: String) : TopicAction
    data object RetryClick : TopicAction
    data object ShareClick : TopicAction
    data class TorrentFileClick(val title: String) : TopicAction
}
