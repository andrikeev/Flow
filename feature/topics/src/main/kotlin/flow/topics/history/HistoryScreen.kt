package flow.topics.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import flow.designsystem.component.DynamicBox
import flow.designsystem.component.Empty
import flow.designsystem.component.FocusableLazyColumn
import flow.designsystem.component.Loading
import flow.designsystem.component.focusableItems
import flow.models.topic.BaseTopic
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.topics.R
import flow.ui.component.TopicListItem
import flow.ui.component.dividedItems
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun HistoryScreen(
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
            imageRes = R.drawable.ill_history,
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
                                val topic = item.topic
                                onAction(
                                    when (topic) {
                                        is BaseTopic -> HistoryAction.TopicClick(topic)
                                        is Torrent -> HistoryAction.TorrentClick(topic)
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
                                val topic = item.topic
                                onAction(
                                    when (topic) {
                                        is BaseTopic -> HistoryAction.TopicClick(topic)
                                        is Torrent -> HistoryAction.TorrentClick(topic)
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
