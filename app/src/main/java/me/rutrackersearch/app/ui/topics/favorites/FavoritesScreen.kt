package me.rutrackersearch.app.ui.topics.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FiberNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.BaseTopic
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.entity.topic.Torrent

@Composable
fun FavoritesScreen(
    onTopicClick: (Topic) -> Unit,
    onTorrentClick: (Torrent) -> Unit,
) {
    FavoritesScreen(
        viewModel = hiltViewModel(),
        onTopicClick = onTopicClick,
        onTorrentClick = onTorrentClick,
    )
}

@Composable
private fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onTopicClick: (Topic) -> Unit,
    onTorrentClick: (Torrent) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    FavoritesScreen(
        state = state,
        onTopicClick = onTopicClick,
        onTorrentClick = onTorrentClick,
    )
}

@Composable
private fun FavoritesScreen(
    state: FavoritesState,
    onTopicClick: (Topic) -> Unit,
    onTorrentClick: (Torrent) -> Unit,
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
                        key = { it.data.id },
                        contentType = { it.data::class },
                    ) {
                        FavoriteTopic(
                            topicModel = it,
                            onTopicClick = onTopicClick,
                            onTorrentClick = onTorrentClick,
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
                        key = { it.data.id },
                        contentType = { it.data::class },
                    ) {
                        FavoriteTopic(
                            topicModel = it,
                            onTopicClick = onTopicClick,
                            onTorrentClick = onTorrentClick,
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
    topic = topicModel.data,
    onClick = {
        when (val topic = topicModel.data) {
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
