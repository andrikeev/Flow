package flow.data.converters

import flow.database.entity.FavoriteTopicEntity
import flow.database.entity.VisitedTopicEntity
import flow.models.Page
import flow.models.topic.Author
import flow.models.topic.BaseTopic
import flow.models.topic.Post
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.TopicPage
import flow.models.topic.Torrent
import flow.models.topic.TorrentData
import flow.models.topic.TorrentStatus
import flow.network.dto.topic.AuthorDto
import flow.network.dto.topic.TopicPageDto
import flow.network.dto.topic.CommentsPageDto
import flow.network.dto.topic.ForumTopicDto
import flow.network.dto.topic.TopicDto
import flow.network.dto.topic.TorrentDataDto
import flow.network.dto.topic.TorrentDto
import flow.network.dto.topic.TorrentStatusDto
import flow.network.dto.user.FavoritesDto

internal fun FavoritesDto.toFavorites(): List<Topic> {
    return topics.map(ForumTopicDto::toTopic)
}

internal fun ForumTopicDto.toTopic(): Topic = when (this) {
    is CommentsPageDto -> BaseTopic(id, title, author?.toAuthor(), category?.toCategory())
    is TopicDto -> BaseTopic(id, title, author?.toAuthor(), category?.toCategory())
    is TorrentDto -> toTorrent()
}

internal fun TopicPageDto.toTopicPage(): TopicPage {
    return TopicPage(
        id = id,
        title = title,
        author = author?.toAuthor(),
        category = category?.toCategory(),
        torrentData = torrentData?.toTorrentData(),
        commentsPage = Page(
            page = commentsPage.page,
            pages = commentsPage.pages,
            items = commentsPage.posts.toPosts(),
        ),
    )
}

internal fun TopicPageDto.toCommentsPage(): Page<Post> {
    return Page(
        page = commentsPage.page,
        pages = commentsPage.pages,
        items = commentsPage.posts.toPosts(),
    )
}

internal fun AuthorDto.toAuthor(): Author = Author(id, name, avatarUrl)

internal fun TorrentDto.toTorrent(): Torrent = Torrent(
    id = id,
    title = title,
    tags = tags,
    author = author?.toAuthor(),
    category = category?.toCategory(),
    status = status?.toStatus(),
    date = date,
    size = size,
    seeds = seeds,
    leeches = leeches,
    magnetLink = magnetLink,
)

internal fun TorrentDataDto.toTorrentData() = TorrentData(
    posterUrl = posterUrl,
    status = status?.toStatus(),
    date = date,
    size = size,
    seeds = seeds,
    leeches = leeches,
    magnetLink = magnetLink,
)

internal fun TorrentStatusDto.toStatus(): TorrentStatus = when (this) {
    TorrentStatusDto.Duplicate -> TorrentStatus.DUPLICATE
    TorrentStatusDto.NotApproved -> TorrentStatus.NOT_APPROVED
    TorrentStatusDto.Checking -> TorrentStatus.CHECKING
    TorrentStatusDto.Approved -> TorrentStatus.APPROVED
    TorrentStatusDto.NeedEdit -> TorrentStatus.NEEDS_EDIT
    TorrentStatusDto.Closed -> TorrentStatus.CLOSED
    TorrentStatusDto.NoDescription -> TorrentStatus.NO_DESCRIPTION
    TorrentStatusDto.Consumed -> TorrentStatus.CONSUMED
}

internal fun FavoriteTopicEntity.toTopic(): Topic =
    if (tags == null && status == null && size == null && seeds == null && leeches == null) {
        BaseTopic(
            id = id,
            title = title,
            author = author,
            category = category,
        )
    } else {
        Torrent(
            id = id,
            title = title,
            author = author,
            category = category,
            tags = tags,
            status = status,
            date = date,
            size = size,
            seeds = seeds,
            leeches = leeches,
            magnetLink = magnetLink,
        )
    }

internal fun FavoriteTopicEntity.toTopicModel(): TopicModel<out Topic> {
    return TopicModel(
        topic = toTopic(),
        isFavorite = true,
        hasUpdate = hasUpdate,
    )
}

internal fun Topic.toFavoriteEntity(): FavoriteTopicEntity {
    return when (this) {
        is BaseTopic -> FavoriteTopicEntity(
            id = id,
            timestamp = System.currentTimeMillis(),
            title = title,
            author = author,
            category = category,
        )

        is Torrent -> FavoriteTopicEntity(
            id = id,
            timestamp = System.currentTimeMillis(),
            title = title,
            author = author,
            category = category,
            tags = tags,
            status = status,
            date = date,
            size = size,
            seeds = seeds,
            leeches = leeches,
            magnetLink = magnetLink,
        )
    }
}

internal fun VisitedTopicEntity.toTopic(): Topic {
    return if (tags == null && status == null && size == null && seeds == null && leeches == null) {
        BaseTopic(
            id = id,
            title = title,
            author = author,
            category = category,
        )
    } else {
        Torrent(
            id = id,
            title = title,
            author = author,
            category = category,
            tags = tags,
            status = status,
            date = date,
            size = size,
            seeds = seeds,
            leeches = leeches,
            magnetLink = magnetLink,
        )
    }
}

internal fun TopicPage.toVisitedEntity(): VisitedTopicEntity {
    val timestamp = System.currentTimeMillis()
    return VisitedTopicEntity(
        id = id,
        timestamp = timestamp,
        title = title,
        author = author,
        category = category,
        status = torrentData?.status,
        size = torrentData?.size,
        seeds = torrentData?.seeds,
        leeches = torrentData?.leeches,
        magnetLink = torrentData?.magnetLink,
    )
}
