package flow.topic.open

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import flow.designsystem.component.AppBar
import flow.designsystem.component.BackButton
import flow.designsystem.component.Error
import flow.designsystem.component.Loading
import flow.designsystem.component.Scaffold
import flow.models.search.Filter
import flow.navigation.viewModel
import flow.topic.topic.TopicScreen
import flow.topic.topic.TopicState
import flow.topic.torrent.TorrentScreen
import flow.topic.torrent.TorrentState
import flow.ui.R
import flow.ui.component.getIllRes
import flow.ui.component.getStringRes
import org.orbitmvi.orbit.compose.collectAsState

@Composable
internal fun OpenTopicScreen(
    viewModel: OpenTopicViewModel,
    back: () -> Unit,
    openCategory: (id: String) -> Unit,
    openComments: (id: String) -> Unit,
    openLogin: () -> Unit,
    openSearch: (filter: Filter) -> Unit,
) {
    val state by viewModel.collectAsState()
    Crossfade(targetState = state) { targetState ->
        when (targetState) {
            is OpenTopicState.Loading -> {
                Scaffold(
                    topBar = { AppBar(navigationIcon = { BackButton(back) }) },
                    content = { padding -> Loading(modifier = Modifier.padding(padding)) },
                )
            }

            is OpenTopicState.Error -> {
                Scaffold(
                    topBar = { AppBar(navigationIcon = { BackButton(back) }) },
                    content = { padding ->
                        Error(
                            modifier = Modifier.padding(padding),
                            titleRes = R.string.error_title,
                            subtitleRes = targetState.error.getStringRes(),
                            imageRes = targetState.error.getIllRes(),
                            onRetryClick = viewModel::retry,
                        )
                    },
                )
            }

            is OpenTopicState.Topic -> TopicScreen(
                viewModel = viewModel(),
                topicState = TopicState(targetState.title),
                back = back,
                openLogin = openLogin,
            )

            is OpenTopicState.Torrent -> TorrentScreen(
                viewModel = viewModel(),
                torrentState = TorrentState(
                    title = targetState.title,
                    posterImage = targetState.posterImage,
                    author = targetState.author,
                    category = targetState.category,
                    status = targetState.status,
                    date = targetState.date,
                    size = targetState.size,
                    seeds = targetState.seeds,
                    leeches = targetState.leeches,
                    magnetLink = targetState.magnetLink,
                    description = targetState.description,
                    showMagnetLink = targetState.showMagnetLink,
                    showTorrentFile = targetState.showTorrentFile,
                ),
                back = back,
                openCategory = openCategory,
                openComments = openComments,
                openLogin = openLogin,
                openSearch = openSearch,
            )
        }
    }
}
