package flow.visited

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
import flow.ui.component.TopicListItem
import flow.ui.component.dividedItems
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun VisitedScreen(
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    VisitedScreen(
        viewModel = hiltViewModel(),
        openTopic = openTopic,
        openTorrent = openTorrent,
    )
}

@Composable
private fun VisitedScreen(
    viewModel: VisitedViewModel,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is VisitedSideEffect.OpenTopic -> openTopic(sideEffect.topic)
            is VisitedSideEffect.OpenTorrent -> openTorrent(sideEffect.torrent)
        }
    }
    val state by viewModel.collectAsState()
    VisitedScreen(state = state, onAction = viewModel::perform)
}

@Composable
private fun VisitedScreen(
    state: VisitedState,
    onAction: (VisitedAction) -> Unit,
) {
    when (state) {
        is VisitedState.Initial -> Loading()
        is VisitedState.Empty -> Empty(
            titleRes = R.string.visited_empty_title,
            subtitleRes = R.string.visited_empty_subtitle,
            imageRes = R.drawable.ill_visited,
        )

        is VisitedState.VisitedList -> DynamicBox(
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
                                        is BaseTopic -> VisitedAction.TopicClick(topic)
                                        is Torrent -> VisitedAction.TorrentClick(topic)
                                    }
                                )
                            },
                            onFavoriteClick = { onAction(VisitedAction.FavoriteClick(item)) },
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
                                        is BaseTopic -> VisitedAction.TopicClick(topic)
                                        is Torrent -> VisitedAction.TorrentClick(topic)
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
