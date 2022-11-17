package flow.topic.open

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import flow.designsystem.component.AppBar
import flow.designsystem.component.BackButton
import flow.designsystem.component.Error
import flow.designsystem.component.Loading
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.ui.component.getIllRes
import flow.ui.component.getStringRes
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import flow.ui.R as UiR

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
                titleRes = UiR.string.error_title,
                subtitleRes = state.error.getStringRes(),
                imageRes = state.error.getIllRes(),
                onRetryClick = { onAction(OpenTopicAction.RetryClick) },
            )
        }
    }
}
