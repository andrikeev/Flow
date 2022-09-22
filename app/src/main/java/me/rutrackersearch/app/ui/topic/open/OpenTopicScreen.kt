package me.rutrackersearch.app.ui.topic.open

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import me.rutrackersearch.app.ui.common.AppBar
import me.rutrackersearch.app.ui.common.BackButton
import me.rutrackersearch.app.ui.common.Error
import me.rutrackersearch.app.ui.common.Loading
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OpenTopicScreen(
    back: () -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    OpenTopicScreen(
        viewModel = hiltViewModel(),
        back = back,
        openTopic = openTopic,
        openTorrent = openTorrent,
    )
}

@Composable
private fun OpenTopicScreen(
    viewModel: OpenTopicViewModel,
    back: () -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OpenTopicSideEffect.Back -> back()
            is OpenTopicSideEffect.OpenTopic -> openTopic(sideEffect.topic)
            is OpenTopicSideEffect.OpenTorrent -> openTorrent(sideEffect.torrent)
        }
    }
    val state by viewModel.collectAsState()
    OpenTopicScreen(state, viewModel::perform)
}

@Composable
private fun OpenTopicScreen(
    state: OpenTopicState,
    onAction: (OpenTopicAction) -> Unit,
) {
    Scaffold(topBar = { AppBar(navigationIcon = { BackButton { onAction(OpenTopicAction.BackClick) } }) }) { padding ->
        when (state) {
            is OpenTopicState.Loading -> Loading(modifier = Modifier.padding(padding))
            is OpenTopicState.Error -> Error(
                modifier = Modifier.padding(padding),
                error = state.error,
                onRetryClick = { onAction(OpenTopicAction.RetryClick) },
            )
        }
    }
}
