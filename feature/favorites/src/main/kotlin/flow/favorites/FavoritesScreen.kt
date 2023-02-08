package flow.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import flow.designsystem.component.DynamicBox
import flow.designsystem.component.Empty
import flow.designsystem.component.FocusableLazyColumn
import flow.designsystem.component.LazyList
import flow.designsystem.component.Loading
import flow.designsystem.component.focusableItems
import flow.designsystem.drawables.FlowIcons
import flow.models.topic.BaseTopic
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import flow.ui.component.TopicListItem
import flow.ui.component.dividedItems
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
            titleRes = R.string.favorites_empty_title,
            subtitleRes = R.string.favorites_empty_subtitle,
            imageRes = R.drawable.ill_favorites,
        )

        is FavoritesState.FavoritesList -> DynamicBox(
            mobileContent = {
                LazyList(
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
                            onTopicClick = { topic -> onAction(FavoritesAction.TopicClick(topic)) },
                            onTorrentClick = { torrent -> onAction(FavoritesAction.TorrentClick(torrent)) },
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
                            onTopicClick = { topic -> onAction(FavoritesAction.TopicClick(topic)) },
                            onTorrentClick = { torrent -> onAction(FavoritesAction.TorrentClick(torrent)) },
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
                imageVector = FlowIcons.NewBadge,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
            )
        }
    },
)
