package flow.data.converters

import flow.database.entity.FavoriteTopicEntity
import flow.database.entity.VisitedTopicEntity
import flow.models.Page
import flow.models.topic.Author
import flow.models.topic.BaseTopic
import flow.models.topic.Post
import flow.models.topic.PostContent
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import flow.models.topic.TorrentDescription
import flow.models.topic.TorrentStatus
import flow.network.dto.ResultDto
import flow.network.dto.topic.AuthorDto
import flow.network.dto.topic.CommentsPageDto
import flow.network.dto.topic.ForumTopicDto
import flow.network.dto.topic.TopicDto
import flow.network.dto.topic.TorrentDescriptionDto
import flow.network.dto.topic.TorrentDto
import flow.network.dto.topic.TorrentStatusDto
import flow.network.dto.user.FavoritesDto


internal fun ResultDto<ForumTopicDto>.toTopic(): Topic {
    require(this is ResultDto.Data)
    return value.toTopic()
}

internal fun ResultDto<CommentsPageDto>.toCommentsPage(): Page<Post> {
    require(this is ResultDto.Data)
    return value.toCommentsPage()
}

internal fun ResultDto<TorrentDto>.toTorrent(): Torrent {
    require(this is ResultDto.Data)
    return value.toTorrent()
}

internal fun ResultDto<FavoritesDto>.toFavorites(): List<Topic> {
    require(this is ResultDto.Data)
    return value.topics.map(ForumTopicDto::toTopic)
}

internal fun ForumTopicDto.toTopic(): Topic = when (this) {
    is CommentsPageDto -> BaseTopic(id, title, author?.toAuthor(), category?.toCategory())
    is TopicDto -> BaseTopic(id, title, author?.toAuthor(), category?.toCategory())
    is TorrentDto -> toTorrent()
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
    description = description?.toDescription(),
)

internal fun CommentsPageDto.toCommentsPage(): Page<Post> = Page(page = page, pages = pages, items = emptyList())

internal fun TorrentStatusDto.toStatus(): TorrentStatus = when (this) {
    TorrentStatusDto.DUPLICATE -> TorrentStatus.DUPLICATE
    TorrentStatusDto.NOT_APPROVED -> TorrentStatus.NOT_APPROVED
    TorrentStatusDto.CHECKING -> TorrentStatus.CHECKING
    TorrentStatusDto.APPROVED -> TorrentStatus.APPROVED
    TorrentStatusDto.NEED_EDIT -> TorrentStatus.NEED_EDIT
    TorrentStatusDto.CLOSED -> TorrentStatus.CLOSED
    TorrentStatusDto.NO_DESCRIPTION -> TorrentStatus.NO_DESCRIPTION
    TorrentStatusDto.CONSUMED -> TorrentStatus.CONSUMED
}

internal fun TorrentDescriptionDto.toDescription(): TorrentDescription = TorrentDescription(PostContent.Default(emptyList()))

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
    val timestamp = System.currentTimeMillis()
    return when (this) {
        is BaseTopic -> FavoriteTopicEntity(
            id = id,
            timestamp = timestamp,
            title = title,
            author = author,
            category = category,
        )

        is Torrent -> FavoriteTopicEntity(
            id = id,
            timestamp = timestamp,
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

internal fun Topic.toVisitedEntity(): VisitedTopicEntity {
    val timestamp = System.currentTimeMillis()
    return when (this) {
        is BaseTopic -> VisitedTopicEntity(
            id = id,
            timestamp = timestamp,
            title = title,
            author = author,
            category = category,
        )

        is Torrent -> VisitedTopicEntity(
            id = id,
            timestamp = timestamp,
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
