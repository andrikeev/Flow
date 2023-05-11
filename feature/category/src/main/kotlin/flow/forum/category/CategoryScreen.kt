package flow.forum.category

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import flow.designsystem.component.AppBar
import flow.designsystem.component.AppBarDefaults
import flow.designsystem.component.PinnedAppBarState
import flow.designsystem.component.BackButton
import flow.designsystem.component.BookmarkButton
import flow.designsystem.component.Dialog
import flow.designsystem.component.DialogState
import flow.designsystem.component.Icon
import flow.designsystem.component.LazyList
import flow.designsystem.component.Scaffold
import flow.designsystem.component.ScrollBackFloatingActionButton
import flow.designsystem.component.SearchButton
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.component.rememberDialogState
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.models.LoadState
import flow.models.forum.Category
import flow.navigation.viewModel
import flow.ui.component.CategoryListItem
import flow.ui.component.TopicListItem
import flow.ui.component.appendItems
import flow.ui.component.dividedItems
import flow.ui.component.emptyItem
import flow.ui.component.errorItem
import flow.ui.component.loadingItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun CategoryScreen(
    back: () -> Unit,
    openCategory: (id: String) -> Unit,
    openLogin: () -> Unit,
    openSearchInput: (categoryId: String) -> Unit,
    openTopic: (id: String) -> Unit,
) = CategoryScreen(
    viewModel = viewModel(),
    back = back,
    openCategory = openCategory,
    openLogin = openLogin,
    openSearchInput = openSearchInput,
    openTopic = openTopic,
)

@Composable
private fun CategoryScreen(
    viewModel: CategoryViewModel,
    back: () -> Unit,
    openCategory: (id: String) -> Unit,
    openLogin: () -> Unit,
    openSearchInput: (categoryId: String) -> Unit,
    openTopic: (id: String) -> Unit,
) {
    val loginDialogState = rememberDialogState()
    LoginDialog(loginDialogState, viewModel::perform)
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is CategorySideEffect.Back -> back()
            is CategorySideEffect.OpenCategory -> openCategory(sideEffect.categoryId)
            is CategorySideEffect.OpenSearch -> openSearchInput(sideEffect.categoryId)
            is CategorySideEffect.OpenTopic -> openTopic(sideEffect.id)
            is CategorySideEffect.ShowLoginDialog -> loginDialogState.show()
            is CategorySideEffect.OpenLogin -> openLogin()
        }
    }
    val state by viewModel.collectAsState()
    CategoryScreen(state, viewModel::perform)
}

@Composable
private fun CategoryScreen(
    state: CategoryPageState,
    onAction: (CategoryAction) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val scrollBehavior = AppBarDefaults.appBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CategoryAppBar(
                state = state.categoryState,
                appBarState = scrollBehavior.appBarState,
                onAction = onAction,
            )
        },
        content = { padding ->
            CategoryScreenList(
                modifier = Modifier.padding(padding),
                state = state,
                scrollState = scrollState,
                onAction = onAction,
            )
        },
        floatingActionButton = { ScrollBackFloatingActionButton(scrollState) }
    )
}

@Composable
private fun CategoryScreenList(
    modifier: Modifier = Modifier,
    state: CategoryPageState,
    scrollState: LazyListState,
    onAction: (CategoryAction) -> Unit,
) = LazyList(
    modifier = modifier,
    state = scrollState,
    contentPadding = PaddingValues(
        top = AppTheme.spaces.medium,
        bottom = AppTheme.spaces.extraLargeBottom,
    ),
    onLastItemVisible = { onAction(CategoryAction.EndOfListReached) },
) {
    when (state.loadStates.refresh) {
        is LoadState.Error -> errorItem(onRetryClick = { onAction(CategoryAction.RetryClick) })
        is LoadState.Loading -> loadingItem()
        is LoadState.NotLoading -> {
            when (state.categoryContent) {
                is CategoryContent.Content -> {
                    dividedItems(
                        items = state.categoryContent.categories,
                        key = Category::id,
                    ) { category ->
                        CategoryListItem(
                            text = category.name,
                            onClick = { onAction(CategoryAction.CategoryClick(category)) }
                        )
                    }
                    dividedItems(
                        items = state.categoryContent.topics,
                        key = { it.topic.id },
                    ) { topicModel ->
                        TopicListItem(
                            topicModel = topicModel,
                            showCategory = false,
                            onClick = { onAction(CategoryAction.TopicClick(topicModel)) },
                            onFavoriteClick = { onAction(CategoryAction.FavoriteClick(topicModel)) },
                        )
                    }
                    appendItems(
                        state = state.loadStates.append,
                        onRetryClick = { onAction(CategoryAction.RetryClick) },
                    )
                }
                is CategoryContent.Empty -> emptyItem(
                    titleRes = R.string.forum_screen_forum_empty_title,
                    subtitleRes = R.string.forum_screen_forum_empty_subtitle,
                    imageRes = flow.ui.R.drawable.ill_empty,
                )
                is CategoryContent.Initial -> loadingItem()
            }
        }
    }
}

@Composable
private fun CategoryAppBar(
    state: CategoryState,
    appBarState: PinnedAppBarState,
    onAction: (CategoryAction) -> Unit,
) {
    AppBar(
        navigationIcon = { BackButton(onClick = { onAction(CategoryAction.BackClick) }) },
        title = {
            if (state is CategoryState.Category) {
                Text(
                    text = state.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = AppTheme.typography.titleMedium,
                )
            }
        },
        actions = {
            SearchButton(onClick = { onAction(CategoryAction.SearchClick) })
            if (state is CategoryState.Category) {
                BookmarkButton(
                    bookmark = state.isBookmark,
                    onClick = { onAction(CategoryAction.BookmarkClick) },
                )
            }
        },
        appBarState = appBarState,
    )
}

@Composable
private fun LoginDialog(
    state: DialogState,
    onAction: (CategoryAction) -> Unit,
) = AnimatedVisibility(
    visible = state.visible,
    exit = ExitTransition.None,
) {
    Dialog(
        icon = {
            Icon(
                icon = FlowIcons.Account,
                contentDescription = null, //TODO: add contentDescription
            )
        },
        title = { Text(stringResource(R.string.forum_screen_login_required_title)) },
        text = { Text(stringResource(R.string.forum_screen_login_required_for_search)) },
        confirmButton = {
            TextButton(
                text = stringResource(flow.designsystem.R.string.designsystem_action_login),
                onClick = {
                    onAction(CategoryAction.LoginClick)
                    state.hide()
                },
            )
        },
        dismissButton = {
            TextButton(
                text = stringResource(flow.designsystem.R.string.designsystem_action_cancel),
                onClick = state::hide,
            )
        },
        onDismissRequest = state::hide,
    )
}
