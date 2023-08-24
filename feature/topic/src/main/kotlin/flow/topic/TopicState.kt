package flow.topic

import flow.domain.model.LoadStates
import flow.models.topic.Post
import flow.models.topic.TorrentData

internal data class TopicState(
    val favoriteState: TopicFavoriteState = TopicFavoriteState.Initial,
    val paginationState: PaginationState = PaginationState.Initial,
    val commentsContent: CommentsContent = CommentsContent.Initial,
    val topicContent: TopicContent = TopicContent.Initial,
    val downloadState: DownloadState = DownloadState.Initial,
    val loadStates: LoadStates = LoadStates.Idle,
)

internal sealed interface TopicFavoriteState {
    data object Initial : TopicFavoriteState
    data class FavoriteState(val favorite: Boolean) : TopicFavoriteState
}

internal sealed interface PaginationState {
    data object Initial : PaginationState
    data object NoPagination : PaginationState
    data class Pagination(
        val page: Int,
        val totalPages: Int,
    ) : PaginationState
}

internal sealed interface CommentsContent {
    data object Initial : CommentsContent
    data object Empty : CommentsContent
    data class Posts(val posts: List<Post>) : CommentsContent
}

internal sealed interface TopicContent {
    data object Initial : TopicContent
    data class Topic(val title: String) : TopicContent
    data class Torrent(
        val title: String,
        val data: TorrentData,
    ) : TopicContent
}

internal sealed interface DownloadState {
    data class Completed(val uri: String) : DownloadState
    data object Error : DownloadState
    data object Initial : DownloadState
    data object Started : DownloadState
}
