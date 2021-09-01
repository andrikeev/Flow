package me.rutrackersearch.app.ui.topics.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
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
import me.rutrackersearch.app.ui.common.Loading
import me.rutrackersearch.app.ui.common.TopicListItem
import me.rutrackersearch.app.ui.common.dividedItems
import me.rutrackersearch.app.ui.common.focusableItems
import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.BaseTopic
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.entity.topic.Torrent

@Composable
fun HistoryScreen(
    onTopicClick: (Topic) -> Unit,
    onTorrentClick: (Torrent) -> Unit,
) {
    HistoryScreen(
        viewModel = hiltViewModel(),
        onTopicClick = onTopicClick,
        onTorrentClick = onTorrentClick,
    )
}

@Composable
private fun HistoryScreen(
    viewModel: HistoryViewModel,
    onTopicClick: (Topic) -> Unit,
    onTorrentClick: (Torrent) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    HistoryScreen(
        state = state,
        onTopicClick = onTopicClick,
        onTorrentClick = onTorrentClick,
        onFavoriteClick = viewModel::onFavoriteClick,
    )
}

@Composable
private fun HistoryScreen(
    state: HistoryState,
    onTopicClick: (Topic) -> Unit,
    onTorrentClick: (Torrent) -> Unit,
    onFavoriteClick: (TopicModel<out Topic>) -> Unit,
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
                        key = { it.data.id },
                        contentType = { it.data::class },
                    ) { item ->
                        TopicListItem(
                            topicModel = item,
                            dimVisited = false,
                            onClick = {
                                when (val topic = item.data) {
                                    is BaseTopic -> onTopicClick(topic)
                                    is Torrent -> onTorrentClick(topic)
                                }
                            },
                            onFavoriteClick = { onFavoriteClick(item) },
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
                    ) { item ->
                        TopicListItem(
                            topicModel = item,
                            dimVisited = false,
                            onClick = {
                                when (val topic = item.data) {
                                    is BaseTopic -> onTopicClick(topic)
                                    is Torrent -> onTorrentClick(topic)
                                }
                            },
                        )
                    }
                }
            },
        )
    }
}
