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
    object Initial : TopicFavoriteState
    data class FavoriteState(val favorite: Boolean) : TopicFavoriteState
}

internal sealed interface PaginationState {
    object Initial : PaginationState
    object NoPagination : PaginationState
    data class Pagination(
        val page: Int,
        val loadedPages: IntRange,
        val totalPages: Int,
    ) : PaginationState
}

internal sealed interface CommentsContent {
    object Initial : CommentsContent
    object Empty : CommentsContent
    data class Posts(val posts: List<Post>) : CommentsContent
}

internal sealed interface TopicContent {
    object Initial : TopicContent
    data class Topic(val title: String) : TopicContent
    data class Torrent(
        val title: String,
        val data: TorrentData,
    ) : TopicContent
}

internal sealed interface DownloadState {
    data class Completed(val uri: String) : DownloadState
    object Error : DownloadState
    object Initial : DownloadState
    object Started : DownloadState
}
