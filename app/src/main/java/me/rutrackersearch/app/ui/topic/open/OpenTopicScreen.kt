package me.rutrackersearch.app.ui.topic.open

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import me.rutrackersearch.app.ui.common.AppBar
import me.rutrackersearch.app.ui.common.BackButton
import me.rutrackersearch.app.ui.common.Error
import me.rutrackersearch.app.ui.common.Loading
import me.rutrackersearch.app.ui.common.Result
import me.rutrackersearch.models.topic.BaseTopic
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent

@Composable
fun OpenTopicScreen(
    onBackClick: () -> Unit,
    onTopicLoaded: (Topic) -> Unit,
    onTorrentLoaded: (Torrent) -> Unit,
) {
    val viewModel: OpenTopicViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state) {
        (state as? Result.Content)?.content?.let { topic ->
            when (topic) {
                is BaseTopic -> onTopicLoaded(topic)
                is Torrent -> onTorrentLoaded(topic)
            }
        }
    }
    OpenTopicScreen(
        state = state,
        onRetry = viewModel::retry,
        onBackClick = onBackClick,
    )
}

@Composable
private fun OpenTopicScreen(
    state: Result<Topic>,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(topBar = { AppBar(navigationIcon = { BackButton(onBackClick) }) }) { padding ->
        when (state) {
            is Result.Loading -> Loading(modifier = Modifier.padding(padding))
            is Result.Error -> Error(
                modifier = Modifier.padding(padding),
                error = state.error,
                onRetryClick = onRetry,
            )
            is Result.Content -> Unit
        }
    }
}
