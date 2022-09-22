package me.rutrackersearch.app.ui.topics.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.Empty
import me.rutrackersearch.app.ui.common.FocusableLazyColumn
import me.rutrackersearch.app.ui.common.Loading
import me.rutrackersearch.app.ui.common.TopicListItem
import me.rutrackersearch.app.ui.common.dividedItems
import me.rutrackersearch.app.ui.common.focusableItems
import me.rutrackersearch.models.topic.BaseTopic
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HistoryScreen(
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    HistoryScreen(
        viewModel = hiltViewModel(),
        openTopic = openTopic,
        openTorrent = openTorrent,
    )
}

@Composable
private fun HistoryScreen(
    viewModel: HistoryViewModel,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HistorySideEffect.OpenTopic -> openTopic(sideEffect.topic)
            is HistorySideEffect.OpenTorrent -> openTorrent(sideEffect.torrent)
        }
    }
    val state by viewModel.collectAsState()
    HistoryScreen(state = state, onAction = viewModel::perform)
}

@Composable
private fun HistoryScreen(
    state: HistoryState,
    onAction: (HistoryAction) -> Unit,
) {
    when (state) {
        is HistoryState.Initial -> Loading()
        is HistoryState.Empty -> Empty(
            titleRes = R.string.topics_history_title,
            subtitleRes = R.string.topics_history_subtitle,
            iconRes = R.drawable.ill_placeholder,
        )

        is HistoryState.HistoryList -> DynamicBox(
            mobileContent = {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    dividedItems(
                        items = state.items,
                        key = { it.topic.id },
                        contentType = { it.topic::class },
                    ) { item ->
                        TopicListItem(
                            topicModel = item,
                            dimVisited = false,
                            onClick = {
                                onAction(
                                    when (item.topic) {
                                        is BaseTopic -> HistoryAction.TopicClick(item.topic)
                                        is Torrent -> HistoryAction.TorrentClick(item.topic)
                                    }
                                )
                            },
                            onFavoriteClick = { onAction(HistoryAction.FavoriteClick(item)) },
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
                    ) { item ->
                        TopicListItem(
                            topicModel = item,
                            dimVisited = false,
                            onClick = {
                                onAction(
                                    when (item.topic) {
                                        is BaseTopic -> HistoryAction.TopicClick(item.topic)
                                        is Torrent -> HistoryAction.TorrentClick(item.topic)
                                    }
                                )
                            },
                        )
                    }
                }
            },
        )
    }
}
