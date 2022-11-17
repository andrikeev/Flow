package flow.topics

import androidx.compose.runtime.Composable
import flow.designsystem.component.Page
import flow.designsystem.component.PagesScreen
import flow.designsystem.drawables.FlowIcons
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.topics.favorites.FavoritesScreen
import flow.topics.history.HistoryScreen

@Composable
fun TopicsScreen(
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    PagesScreen(
        pages = listOf(
            Page(
                labelResId = R.string.topics_history_title,
                icon = FlowIcons.History,
            ) {
                HistoryScreen(
                    openTopic = openTopic,
                    openTorrent = openTorrent,
                )
            },
            Page(
                labelResId = R.string.topics_favorites_title,
                icon = FlowIcons.Favorite,
            ) {
                FavoritesScreen(
                    openTopic = openTopic,
                    openTorrent = openTorrent,
                )
            },
        )
    )
}
