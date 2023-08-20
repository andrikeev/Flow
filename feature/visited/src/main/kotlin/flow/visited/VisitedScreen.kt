package flow.visited

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import flow.designsystem.component.Empty
import flow.designsystem.component.LazyList
import flow.designsystem.component.Loading
import flow.designsystem.component.LocalSnackbarHostState
import flow.designsystem.theme.AppTheme
import flow.navigation.viewModel
import flow.ui.component.TopicListItem
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
    val snackbarHost = LocalSnackbarHostState.current
    val favoriteToggleError = stringResource(flow.ui.R.string.error_title)
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is VisitedSideEffect.OpenTopic -> openTopic(sideEffect.id)
            is VisitedSideEffect.ShowFavoriteToggleError -> snackbarHost.showSnackbar(favoriteToggleError)
        }
    }
    val state by viewModel.collectAsState()
    VisitedScreen(state = state, onAction = viewModel::perform)
}

@Composable
private fun VisitedScreen(
    state: VisitedState,
    onAction: (VisitedAction) -> Unit,
) = when (state) {
    is VisitedState.Initial -> Loading()
    is VisitedState.Empty -> Empty(
        titleRes = R.string.visited_empty_title,
        subtitleRes = R.string.visited_empty_subtitle,
        imageRes = R.drawable.ill_visited,
    )
    is VisitedState.VisitedList -> LazyList(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = AppTheme.spaces.medium),
    ) {
        items(items = state.items) { model ->
            TopicListItem(
                modifier = Modifier.padding(
                    horizontal = AppTheme.spaces.mediumLarge,
                    vertical = AppTheme.spaces.mediumSmall,
                ),
                topicModel = model,
                dimVisited = false,
                onClick = { onAction(VisitedAction.TopicClick(model)) },
                onFavoriteClick = { onAction(VisitedAction.FavoriteClick(model)) },
            )
        }
    }
}
