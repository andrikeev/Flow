package flow.visited

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import flow.designsystem.component.Empty
import flow.designsystem.component.Loading
import flow.designsystem.theme.AppTheme
import flow.navigation.viewModel
import flow.ui.component.TopicListItem
import flow.ui.component.dividedItems
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun VisitedScreen(
    openTopic: (id: String) -> Unit,
) = VisitedScreen(
    viewModel = viewModel(),
    openTopic = openTopic,
)

@Composable
private fun VisitedScreen(
    viewModel: VisitedViewModel,
    openTopic: (id: String) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is VisitedSideEffect.OpenTopic -> openTopic(sideEffect.id)
        }
    }
    val state by viewModel.collectAsState()
    VisitedScreen(state = state, onAction = viewModel::perform)
}

@Composable
private fun VisitedScreen(
    state: VisitedState,
    onAction: (VisitedAction) -> Unit,
) = Crossfade(
    targetState = state,
    label = "VisitedScreen_Crossfade",
) { targetState ->
    when (targetState) {
        is VisitedState.Initial -> Loading()
        is VisitedState.Empty -> Empty(
            titleRes = R.string.visited_empty_title,
            subtitleRes = R.string.visited_empty_subtitle,
            imageRes = R.drawable.ill_visited,
        )
        is VisitedState.VisitedList -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = AppTheme.spaces.medium),
        ) {
            dividedItems(
                items = targetState.items,
                key = { it.topic.id },
                contentType = { it.topic::class },
            ) { item ->
                TopicListItem(
                    topicModel = item,
                    dimVisited = false,
                    onClick = { onAction(VisitedAction.TopicClick(item)) },
                    onFavoriteClick = { onAction(VisitedAction.FavoriteClick(item)) },
                )
            }
        }
    }
}
