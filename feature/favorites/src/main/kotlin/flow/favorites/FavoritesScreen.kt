package flow.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import flow.designsystem.component.Empty
import flow.designsystem.component.Icon
import flow.designsystem.component.LazyList
import flow.designsystem.component.Loading
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.navigation.viewModel
import flow.ui.component.TopicListItem
import flow.ui.component.dividedItems
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FavoritesScreen(
    openTopic: (id: String) -> Unit,
) = FavoritesScreen(
    viewModel = viewModel(),
    openTopic = openTopic,
)

@Composable
private fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    openTopic: (id: String) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FavoritesSideEffect.OpenTopic -> openTopic(sideEffect.id)
        }
    }
    val state by viewModel.collectAsState()
    FavoritesScreen(state, viewModel::perform)
}

@Composable
private fun FavoritesScreen(
    state: FavoritesState,
    onAction: (FavoritesAction) -> Unit,
) = when (state) {
    is FavoritesState.Initial -> Loading()
    is FavoritesState.Empty -> Empty(
        titleRes = R.string.favorites_empty_title,
        subtitleRes = R.string.favorites_empty_subtitle,
        imageRes = R.drawable.ill_favorites,
    )
    is FavoritesState.FavoritesList -> LazyList(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = AppTheme.spaces.medium),
    ) {
        dividedItems(
            items = state.items,
            key = { it.topic.id },
            contentType = { it.topic::class },
        ) {
            FavoriteTopic(
                topicModel = it,
                onClick = { onAction(FavoritesAction.TopicClick(it)) },
            )
        }
    }
}

@Composable
private fun FavoriteTopic(
    topicModel: TopicModel<out Topic>,
    onClick: () -> Unit,
) = TopicListItem(
    topic = topicModel.topic,
    onClick = onClick,
    action = {
        if (topicModel.hasUpdate) {
            Icon(
                icon = FlowIcons.NewBadge,
                tint = AppTheme.colors.primary,
                contentDescription = null,
            )
        }
    },
)
