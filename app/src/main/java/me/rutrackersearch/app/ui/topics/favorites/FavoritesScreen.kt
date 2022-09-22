package me.rutrackersearch.app.ui.topics.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FiberNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.Empty
import me.rutrackersearch.app.ui.common.FocusableLazyColumn
import me.rutrackersearch.app.ui.common.LazyColumn
import me.rutrackersearch.app.ui.common.Loading
import me.rutrackersearch.app.ui.common.TopicListItem
import me.rutrackersearch.app.ui.common.dividedItems
import me.rutrackersearch.app.ui.common.focusableItems
import me.rutrackersearch.models.topic.BaseTopic
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FavoritesScreen(
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    FavoritesScreen(
        viewModel = hiltViewModel(),
        openTopic = openTopic,
        openTorrent = openTorrent,
    )
}

@Composable
private fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FavoritesSideEffect.OpenTopic -> openTopic(sideEffect.topic)
            is FavoritesSideEffect.OpenTorrent -> openTorrent(sideEffect.torrent)
        }
    }
    val state by viewModel.collectAsState()
    FavoritesScreen(state, viewModel::perform)
}

@Composable
private fun FavoritesScreen(
    state: FavoritesState,
    onAction: (FavoritesAction) -> Unit,
) {
    when (state) {
        is FavoritesState.Initial -> Loading()
        is FavoritesState.Empty -> Empty(
            titleRes = R.string.topics_favorites_title,
            subtitleRes = R.string.topics_favorites_subtitle,
            iconRes = R.drawable.ill_favorites,
        )
        is FavoritesState.FavoritesList -> DynamicBox(
            mobileContent = {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    dividedItems(
                        items = state.items,
                        key = { it.topic.id },
                        contentType = { it.topic::class },
                    ) {
                        FavoriteTopic(
                            topicModel = it,
                            onTopicClick = { topic -> onAction(FavoritesAction.TopicClick(topic))},
                            onTorrentClick = { torrent -> onAction(FavoritesAction.TorrentClick(torrent))},
                        )
                    }
                }
            },
            tvContent = {
                FocusableLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(32.dp),
                    refocusFirst = false,
                ) {
                    focusableItems(
                        items = state.items,
                        key = { it.topic.id },
                        contentType = { it.topic::class },
                    ) {
                        FavoriteTopic(
                            topicModel = it,
                            onTopicClick = { topic -> onAction(FavoritesAction.TopicClick(topic))},
                            onTorrentClick = { torrent -> onAction(FavoritesAction.TorrentClick(torrent))},
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun FavoriteTopic(
    topicModel: TopicModel<out Topic>,
    onTopicClick: (Topic) -> Unit,
    onTorrentClick: (Torrent) -> Unit,
) = TopicListItem(
    topic = topicModel.topic,
    onClick = {
        when (val topic = topicModel.topic) {
            is BaseTopic -> onTopicClick(topic)
            is Torrent -> onTorrentClick(topic)
        }
    },
    action = {
        if (topicModel.hasUpdate) {
            Icon(
                imageVector = Icons.Outlined.FiberNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
            )
        }
    },
)
