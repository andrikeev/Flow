package me.rutrackersearch.app.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.entity.topic.Author
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.entity.topic.Torrent
import me.rutrackersearch.domain.entity.topic.TorrentStatus

@Composable
fun TopicListItem(
    topicModel: TopicModel<out Topic>,
    showCategory: Boolean = true,
    dimVisited: Boolean = true,
    highlightNew: Boolean = false,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    val (topic, isVisited, isFavorite, isNew) = topicModel
    val containerColor by animateColorAsState(
        with(MaterialTheme.colorScheme) {
            if (highlightNew && isNew) surfaceVariant else surface
        }
    )
    val contentColor = if (dimVisited && isVisited) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.contentColorFor(containerColor)
    }
    TopicListItem(
        topic = topic,
        showCategory = showCategory,
        containerColor = containerColor,
        contentColor = contentColor,
        action = {
            IconButton(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .then(Modifier.size(24.dp)),
                onClick = onFavoriteClick,
                imageVector = if (isFavorite) {
                    Icons.Outlined.Favorite
                } else {
                    Icons.Outlined.FavoriteBorder
                },
                tint = MaterialTheme.colorScheme.tertiary,
            )
        },
        onClick = onClick,
    )
}

@Composable
fun TopicListItem(
    topicModel: TopicModel<out Topic>,
    showCategory: Boolean = true,
    dimVisited: Boolean = true,
    highlightNew: Boolean = false,
    onClick: () -> Unit,
) {
    val (topic, isVisited, _, isNew) = topicModel
    val containerColor by animateColorAsState(
        with(MaterialTheme.colorScheme) {
            if (highlightNew && isNew) surfaceVariant else surface
        }
    )
    val contentColor = if (dimVisited && isVisited) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.contentColorFor(containerColor)
    }
    when (topic) {
        is Torrent -> Torrent(
            torrent = topic,
            showCategory = showCategory,
            containerColor = containerColor,
            contentColor = contentColor,
            onClick = onClick,
        )
        else -> Topic(
            topic = topic,
            showCategory = showCategory,
            containerColor = containerColor,
            contentColor = contentColor,
            onClick = onClick,
        )
    }
}

@Composable
fun TopicListItem(
    topic: Topic,
    showCategory: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    action: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    when (topic) {
        is Torrent -> Torrent(
            torrent = topic,
            showCategory = showCategory,
            containerColor = containerColor,
            contentColor = contentColor,
            action = action,
            onClick = onClick,
        )
        else -> Topic(
            topic = topic,
            showCategory = showCategory,
            containerColor = containerColor,
            contentColor = contentColor,
            action = action,
            onClick = onClick,
        )
    }
}


@Composable
private fun Topic(
    modifier: Modifier = Modifier,
    topic: Topic,
    showCategory: Boolean = true,
    containerColor: Color,
    contentColor: Color,
    action: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(CenterVertically)
            ) {
                topic.category?.takeIf { showCategory }?.let { category ->
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                        )
                    )
                }
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.bodyMedium,
                )
                topic.author?.let { author ->
                    Text(
                        text = author.name,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.secondary,
                        ),
                    )
                }
            }
            action?.invoke()
        }
    }
}

@Composable
private fun Torrent(
    modifier: Modifier = Modifier,
    torrent: Torrent,
    showCategory: Boolean = true,
    containerColor: Color,
    contentColor: Color,
    action: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(CenterVertically)
                ) {
                    torrent.category?.takeIf { showCategory }?.let { category ->
                        Text(
                            modifier = Modifier.padding(bottom = 4.dp),
                            text = category.name,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                    Text(
                        text = torrent.title,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                action?.invoke()
            }
            torrent.tags?.takeIf(String::isNotBlank)?.let { tags ->
                Text(
                    text = tags,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.outline,
                    ),
                )
            }
            torrent.author?.let { author ->
                Text(
                    text = author.name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.secondary,
                    ),
                )
            }
            ProvideTextStyle(value = MaterialTheme.typography.labelMedium) {
                TorrentStatus(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                        .height(16.dp),
                    torrent = torrent
                )
            }
        }
    }
}

@Composable
@Preview
private fun TopicListItem() {
    Column {
        TopicListItem(
            topicModel = TopicModel(
                data = Torrent(
                    id = "1",
                    title = "Сияние / The Shining (Стэнли Кубрик / S 23 3 Kubrick) 2x MVO + DVO + 4x AVO (Володарский, Гаврилов, Живов, Кузнецов) + VO + Sub Rus, Eng + Comm + Original Eng",
                    author = Author(name = "qooble"),
                    category = Category(id = "1", name = "UHD Video"),
                    tags = "[1980, США, ужасы, триллер, UHD BDRemux 2160p] [US Version]",
                    status = TorrentStatus.APPROVED,
                    date = 1632306880063,
                    size = "92.73 GB",
                    seeds = 28,
                    leeches = 0,
                )
            ),
            onClick = {},
            onFavoriteClick = {},
        )
        TopicListItem(
            topicModel = TopicModel(
                data = Torrent(
                    id = "1",
                    title = "Сияние / The Shining (Стэнли Кубрик / S 23 3 Kubrick) 2x MVO + DVO + 4x AVO (Володарский, Гаврилов, Живов, Кузнецов) + VO + Sub Rus, Eng + Comm + Original Eng",
                    author = Author(name = "qooble"),
                    category = Category(id = "1", name = "UHD Video"),
                    tags = "[1980, США, ужасы, триллер, UHD BDRemux 2160p] [US Version]",
                    status = TorrentStatus.APPROVED,
                    date = 1632306880063,
                    size = "92.73 GB",
                    seeds = 28,
                    leeches = 0,
                ),
                isVisited = true,
            ),
            dimVisited = true,
            onClick = {},
            onFavoriteClick = {},
        )
        TopicListItem(
            topicModel = TopicModel(
                data = Torrent(
                    id = "1",
                    title = "Сияние / The Shining (Стэнли Кубрик / S 23 3 Kubrick) 2x MVO + DVO + 4x AVO (Володарский, Гаврилов, Живов, Кузнецов) + VO + Sub Rus, Eng + Comm + Original Eng",
                    author = Author(name = "qooble"),
                    category = Category(id = "1", name = "UHD Video"),
                    tags = "[1980, США, ужасы, триллер, UHD BDRemux 2160p] [US Version]",
                    status = TorrentStatus.APPROVED,
                    date = 1632306880063,
                    size = "92.73 GB",
                    seeds = 28,
                    leeches = 0,
                ),
                isFavorite = true,
            ),
            showCategory = false,
            onClick = {},
            onFavoriteClick = {},
        )
        TopicListItem(
            topicModel = TopicModel(
                data = Torrent(
                    id = "1",
                    title = "Сияние / The Shining (Стэнли Кубрик / S 23 3 Kubrick) 2x MVO + DVO + 4x AVO (Володарский, Гаврилов, Живов, Кузнецов) + VO + Sub Rus, Eng + Comm + Original Eng",
                    author = Author(name = "qooble"),
                    category = Category(id = "1", name = "UHD Video"),
                    tags = "[1980, США, ужасы, триллер, UHD BDRemux 2160p] [US Version]",
                    status = TorrentStatus.APPROVED,
                    date = 1632306880063,
                    size = "92.73 GB",
                    seeds = 28,
                    leeches = 0,
                ),
                isNew = true,
            ),
            highlightNew = true,
            onClick = {},
            onFavoriteClick = {},
        )
    }
}
