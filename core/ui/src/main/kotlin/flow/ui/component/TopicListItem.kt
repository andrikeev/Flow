package flow.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import flow.designsystem.component.Body
import flow.designsystem.component.BodySmall
import flow.designsystem.component.FavoriteButton
import flow.designsystem.component.Label
import flow.designsystem.component.LazyList
import flow.designsystem.component.ProvideTextStyle
import flow.designsystem.component.Surface
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.contentColorFor
import flow.models.forum.Category
import flow.models.topic.Author
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import flow.models.topic.TorrentStatus

@Composable
fun TopicListItem(
    topicModel: TopicModel<out Topic>,
    showCategory: Boolean = true,
    dimVisited: Boolean = true,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    val (topic, isVisited, isFavorite) = topicModel
    val alpha = if (dimVisited && isVisited) 0.5f else 1f
    TopicListItem(
        modifier = Modifier.alpha(alpha),
        topic = topic,
        showCategory = showCategory,
        action = {
            FavoriteButton(
                modifier = Modifier.size(AppTheme.sizes.medium),
                favorite = isFavorite,
                onClick = onFavoriteClick,
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
    onClick: () -> Unit,
) {
    val (topic, isVisited) = topicModel
    val alpha = if (dimVisited && isVisited) 0.5f else 1f
    TopicListItem(
        modifier = Modifier.alpha(alpha),
        topic = topic,
        showCategory = showCategory,
        onClick = onClick,
    )
}

@Composable
fun TopicListItem(
    modifier: Modifier = Modifier,
    topic: Topic,
    showCategory: Boolean = true,
    containerColor: Color = AppTheme.colors.surface,
    contentColor: Color = AppTheme.colors.contentColorFor(containerColor),
    action: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    when (topic) {
        is Torrent -> Torrent(
            modifier = modifier,
            torrent = topic,
            showCategory = showCategory,
            contentColor = contentColor,
            action = action,
            onClick = onClick,
        )

        else -> Topic(
            modifier = modifier,
            topic = topic,
            showCategory = showCategory,
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
    contentColor: Color,
    action: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) = Surface(
    modifier = modifier,
    onClick = onClick,
    contentColor = contentColor,
) {
    Row(
        modifier = Modifier.padding(
            horizontal = AppTheme.spaces.large,
            vertical = AppTheme.spaces.mediumLarge,
        ),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .align(CenterVertically)
        ) {
            topic.category?.takeIf { showCategory }?.let { category ->
                Label(
                    text = category.name,
                    color = AppTheme.colors.primary,
                )
            }
            Body(topic.title)
            topic.author?.let { author ->
                BodySmall(
                    text = author.name,
                    color = AppTheme.colors.primary,
                )
            }
        }
        action?.invoke()
    }
}

@Composable
private fun Torrent(
    modifier: Modifier = Modifier,
    torrent: Torrent,
    showCategory: Boolean = true,
    contentColor: Color,
    action: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        contentColor = contentColor,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = AppTheme.spaces.large,
                vertical = AppTheme.spaces.mediumLarge,
            ),
        ) {
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(CenterVertically)
                ) {
                    torrent.category?.takeIf { showCategory }?.let { category ->
                        Label(
                            modifier = Modifier.padding(bottom = AppTheme.spaces.small),
                            text = category.name,
                            color = AppTheme.colors.primary,
                        )
                    }
                    Body(torrent.title)
                }
                action?.invoke()
            }
            torrent.tags?.takeIf(String::isNotBlank)?.let { tags ->
                BodySmall(
                    text = tags,
                    color = AppTheme.colors.outline,
                )
            }
            torrent.author?.let { author ->
                BodySmall(
                    text = author.name,
                    color = AppTheme.colors.primary,
                )
            }
            ProvideTextStyle(value = AppTheme.typography.labelMedium) {
                TorrentStatus(
                    modifier = Modifier
                        .padding(top = AppTheme.spaces.small)
                        .fillMaxWidth()
                        .height(AppTheme.sizes.small),
                    torrent = torrent
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun TopicListItem() {
    LazyList {
        item {
            TopicListItem(
                topicModel = TopicModel(
                    topic = Torrent(
                        id = "1",
                        title = "Сияние / The Shining (Стэнли Кубрик / S 23 3 Kubrick) 2x MVO + DVO + 4x AVO (Володарский, Гаврилов, Живов, Кузнецов) + VO + Sub Rus, Eng + Comm + Original Eng",
                        author = Author(name = "qooble"),
                        category = Category(id = "1", name = "UHD Video"),
                        tags = "[1980, США, ужасы, триллер, UHD BDRemux 2160p] [US Version]",
                        status = TorrentStatus.APPROVED,
                        date = 1632306880,
                        size = "92.73 GB",
                        seeds = 28,
                        leeches = 0,
                    )
                ),
                onClick = {},
                onFavoriteClick = {},
            )
        }
        item {
            TopicListItem(
                topicModel = TopicModel(
                    topic = Torrent(
                        id = "1",
                        title = "Сияние / The Shining (Стэнли Кубрик / S 23 3 Kubrick) 2x MVO + DVO + 4x AVO (Володарский, Гаврилов, Живов, Кузнецов) + VO + Sub Rus, Eng + Comm + Original Eng",
                        author = Author(name = "qooble"),
                        category = Category(id = "1", name = "UHD Video"),
                        tags = "[1980, США, ужасы, триллер, UHD BDRemux 2160p] [US Version]",
                        status = TorrentStatus.APPROVED,
                        date = 1632306880,
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
        }
        item {
            TopicListItem(
                topicModel = TopicModel(
                    topic = Torrent(
                        id = "1",
                        title = "Сияние / The Shining (Стэнли Кубрик / S 23 3 Kubrick) 2x MVO + DVO + 4x AVO (Володарский, Гаврилов, Живов, Кузнецов) + VO + Sub Rus, Eng + Comm + Original Eng",
                        author = Author(name = "qooble"),
                        category = Category(id = "1", name = "UHD Video"),
                        tags = "[1980, США, ужасы, триллер, UHD BDRemux 2160p] [US Version]",
                        status = TorrentStatus.APPROVED,
                        date = 1632306880,
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
        }
        item {
            TopicListItem(
                topicModel = TopicModel(
                    topic = Torrent(
                        id = "1",
                        title = "Сияние / The Shining (Стэнли Кубрик / S 23 3 Kubrick) 2x MVO + DVO + 4x AVO (Володарский, Гаврилов, Живов, Кузнецов) + VO + Sub Rus, Eng + Comm + Original Eng",
                        author = Author(name = "qooble"),
                        category = Category(id = "1", name = "UHD Video"),
                        tags = "[1980, США, ужасы, триллер, UHD BDRemux 2160p] [US Version]",
                        status = TorrentStatus.APPROVED,
                        date = 1632306880,
                        size = "92.73 GB",
                        seeds = 28,
                        leeches = 0,
                    ),
                    isNew = true,
                ),
                onClick = {},
                onFavoriteClick = {},
            )
        }

    }
}
