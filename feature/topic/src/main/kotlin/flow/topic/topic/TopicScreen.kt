package flow.topic.topic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flow.designsystem.component.AddCommentFloatingActionButton
import flow.designsystem.component.AppBarDefaults
import flow.designsystem.component.AppBarState
import flow.designsystem.component.BackButton
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.BodySmall
import flow.designsystem.component.CircularProgressIndicator
import flow.designsystem.component.Dialog
import flow.designsystem.component.DialogState
import flow.designsystem.component.ExpandableAppBar
import flow.designsystem.component.FavoriteButton
import flow.designsystem.component.Icon
import flow.designsystem.component.IconButton
import flow.designsystem.component.LazyList
import flow.designsystem.component.LocalSnackbarHostState
import flow.designsystem.component.Scaffold
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.component.TextField
import flow.designsystem.component.ThemePreviews
import flow.designsystem.component.rememberDialogState
import flow.designsystem.component.rememberExpandState
import flow.designsystem.component.rememberFocusRequester
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.designsystem.utils.RunOnFirstComposition
import flow.models.LoadState
import flow.models.topic.Author
import flow.models.topic.Post
import flow.models.topic.TextContent
import flow.topic.R
import flow.ui.component.Avatar
import flow.ui.component.Post
import flow.ui.component.appendItems
import flow.ui.component.dividedItems
import flow.ui.component.emptyItem
import flow.ui.component.errorItem
import flow.ui.component.loadingItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import flow.designsystem.R as dsR
import flow.ui.R as uiR

@Composable
internal fun TopicScreen(
    viewModel: TopicViewModel,
    back: () -> Unit,
    openLogin: () -> Unit,
) {
    val resources = LocalContext.current.resources
    val snackbarState = LocalSnackbarHostState.current
    val addCommentDialogState = rememberDialogState()
    AddCommentDialog(addCommentDialogState, viewModel::perform)
    val loginRequiredDialogState = rememberDialogState()
    LoginRequiredDialog(loginRequiredDialogState, viewModel::perform)
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TopicSideEffect.Back -> back()
            is TopicSideEffect.OpenLogin -> openLogin()
            is TopicSideEffect.ShowAddCommentDialog -> addCommentDialogState.show()
            is TopicSideEffect.ShowAddCommentError -> {
                snackbarState.showSnackbar(resources.getString(uiR.string.error_something_goes_wrong))
            }

            is TopicSideEffect.ShowLoginRequired -> loginRequiredDialogState.show()
        }
    }
    val state by viewModel.collectAsState()
    MobileTopicScreen(state, viewModel::perform)
}

@Composable
private fun MobileTopicScreen(
    state: TopicPageState,
    onAction: (TopicAction) -> Unit,
) {
    val scrollBehavior = AppBarDefaults.appBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopicAppBar(
                state = state,
                appBarState = scrollBehavior.state,
                onAction = onAction
            )
        },
        floatingActionButton = {
            AddCommentFloatingActionButton(onClick = { onAction(TopicAction.AddCommentClick) })
        },
    ) { padding ->
        val scrollState = rememberLazyListState()
        PostsList(
            modifier = Modifier.padding(padding),
            state = state,
            scrollState = scrollState,
            onAction = onAction
        )
    }
}

@Composable
private fun TopicAppBar(
    state: TopicPageState,
    appBarState: AppBarState,
    onAction: (TopicAction) -> Unit,
) {
    val expandState = rememberExpandState()
    ExpandableAppBar(
        navigationIcon = { BackButton { onAction(TopicAction.BackClick) } },
        title = {
            when (val topicState = state.topicState) {
                is TopicState.Initial -> Unit
                is TopicState.Topic -> Text(
                    text = topicState.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = AppTheme.typography.titleSmall,
                )
            }
        },
        actions = {
            Crossfade(
                targetState = state.topicState,
                label = "TopicFavoriteButton_Crossfade",
            ) { topicState ->
                when (topicState) {
                    is TopicState.Initial -> Unit
                    is TopicState.Topic -> FavoriteButton(
                        favorite = topicState.isFavorite,
                        onClick = { onAction(TopicAction.FavoriteClick) },
                    )
                }
            }
            when (state.paginationState) {
                is PaginationState.Initial -> Unit
                is PaginationState.Pagination -> {
                    AnimatedVisibility(visible = expandState.expanded || state.paginationState.pages > 1) {
                        val rotation by animateFloatAsState(
                            targetValue = if (expandState.expanded) 180f else 0f,
                            label = "TopicPagingActionButton_Rotation",
                        )
                        IconButton(
                            modifier = Modifier.rotate(rotation),
                            icon = if (expandState.expanded) {
                                FlowIcons.Expand
                            } else {
                                FlowIcons.Pages
                            },
                            contentDescription = null, //TODO: add contentDescription
                            onClick = expandState::toggle,
                        )
                    }
                }
            }
        },
        expandableContent = {
            Crossfade(
                targetState = state.paginationState,
                label = "TopicPaginationBar_Crossfade",
            ) { pagingState ->
                when (pagingState) {
                    is PaginationState.Initial -> Unit
                    is PaginationState.Pagination -> Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.spaces.medium),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        fun goToPage(page: Int) = onAction(TopicAction.GoToPage(page))
                        val notLoading = state.loadStates.refresh != LoadState.Loading
                        IconButton(
                            icon = FlowIcons.FirstPage,
                            contentDescription = null, //TODO: add contentDescription
                            enabled = notLoading && pagingState.page != 1,
                            onClick = { goToPage(1) },
                        )
                        IconButton(
                            icon = FlowIcons.PrevPage,
                            contentDescription = null, //TODO: add contentDescription
                            enabled = notLoading && pagingState.page != 1,
                            onClick = { goToPage(pagingState.page - 1) },
                        )
                        Box(
                            modifier = Modifier.width(AppTheme.sizes.default),
                            contentAlignment = Alignment.Center,
                        ) {
                            Crossfade(
                                targetState = notLoading,
                                label = "TopicPaginationBar_CurrentPage_Crossfade",
                            ) { showPage ->
                                if (showPage) {
                                    Text(
                                        text = buildString {
                                            append(pagingState.page)
                                            if (pagingState.pages > 0) {
                                                append('/', pagingState.pages)
                                            }
                                        },
                                        style = AppTheme.typography.labelLarge,
                                    )
                                } else {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(AppTheme.sizes.small),
                                        strokeWidth = 1.dp,
                                    )
                                }
                            }
                        }
                        IconButton(
                            icon = FlowIcons.NextPage,
                            contentDescription = null, //TODO: add contentDescription
                            enabled = notLoading && pagingState.page != pagingState.pages,
                            onClick = { goToPage(pagingState.page + 1) },
                        )
                        IconButton(
                            icon = FlowIcons.LastPage,
                            contentDescription = null, //TODO: add contentDescription
                            enabled = notLoading && pagingState.page != pagingState.pages,
                            onClick = { goToPage(pagingState.pages) },
                        )
                    }
                }
            }
        },
        expanded = expandState.expanded,
        appBarState = appBarState,
    )
}

@Composable
private fun PostsList(
    modifier: Modifier = Modifier,
    state: TopicPageState,
    scrollState: LazyListState,
    onAction: (TopicAction) -> Unit,
) = LazyList(
    modifier = modifier,
    state = scrollState,
    contentPadding = PaddingValues(
        top = AppTheme.spaces.medium,
        bottom = AppTheme.spaces.extraLargeBottom,
    ),
    onFirstItemVisible = { onAction(TopicAction.ListTopReached) },
    onLastItemVisible = { onAction(TopicAction.ListBottomReached) },
    onLastVisibleIndexChanged = { index -> onAction(TopicAction.LastVisibleIndexChanged(index)) }
) {
    when (state.loadStates.refresh) {
        is LoadState.Error -> errorItem(onRetryClick = { onAction(TopicAction.RetryClick) })
        is LoadState.Loading -> loadingItem()
        is LoadState.NotLoading -> {
            when (val topicContent = state.topicContent) {
                is TopicContent.Empty -> emptyItem(
                    titleRes = R.string.topic_empty_title,
                    subtitleRes = R.string.topic_empty_subtitle,
                    imageRes = uiR.drawable.ill_empty,
                )

                is TopicContent.Initial -> loadingItem()
                is TopicContent.Posts -> {
                    dividedItems(
                        items = topicContent.posts,
                        key = Post::id,
                        contentType = { it::class },
                        itemContent = { post -> PostItem(post = post) },
                    )
                    appendItems(
                        state = state.loadStates.append,
                        onRetryClick = { onAction(TopicAction.RetryClick) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PostItem(
    modifier: Modifier = Modifier,
    post: Post,
) = Column(
    modifier = modifier.padding(
        horizontal = AppTheme.spaces.large,
        vertical = AppTheme.spaces.mediumLarge,
    ),
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(AppTheme.sizes.default)) {
            Avatar(post.author.avatarUrl)
        }
        Column(modifier = Modifier.padding(start = AppTheme.spaces.medium)) {
            BodyLarge(
                text = post.author.name,
                color = AppTheme.colors.primary,
            )
            BodySmall(
                text = post.date,
                color = AppTheme.colors.outline,
            )
        }
    }
    Post(
        modifier = Modifier.padding(top = AppTheme.spaces.small),
        content = post.content,
    )
}

@Composable
private fun LoginRequiredDialog(
    dialogState: DialogState,
    onAction: (TopicAction) -> Unit,
) {
    if (dialogState.visible) {
        Dialog(
            icon = {
                Icon(
                    icon = FlowIcons.Account,
                    contentDescription = null,
                )
            },
            title = { Text(stringResource(R.string.topics_login_required_title)) },
            text = { Text(stringResource(R.string.topics_login_required_for_comment)) },
            confirmButton = {
                TextButton(
                    text = stringResource(dsR.string.designsystem_action_login),
                    onClick = {
                        dialogState.hide()
                        onAction(TopicAction.LoginClick)
                    },
                )
            },
            dismissButton = {
                TextButton(
                    text = stringResource(dsR.string.designsystem_action_cancel),
                    onClick = dialogState::hide,
                )
            },
            onDismissRequest = dialogState::hide,
        )
    }
}

@Composable
private fun AddCommentDialog(
    dialogState: DialogState,
    onAction: (TopicAction) -> Unit,
) {
    if (dialogState.visible) {
        var textValue by remember { mutableStateOf("") }
        Dialog(
            title = { Text(stringResource(R.string.topic_new_comment)) },
            text = {
                val focusRequester = rememberFocusRequester()
                RunOnFirstComposition(focusRequester::requestFocus)
                TextField(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 96.dp)
                        .focusRequester(focusRequester),
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text(stringResource(R.string.topic_new_comment)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        autoCorrect = true,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            dialogState.hide()
                            onAction(TopicAction.AddComment(textValue))
                        }
                    ),
                    maxLines = 3,
                )
            },
            confirmButton = {
                TextButton(
                    text = stringResource(dsR.string.designsystem_action_send),
                    onClick = {
                        dialogState.hide()
                        onAction(TopicAction.AddComment(textValue))
                    },
                )
            },
            dismissButton = {
                TextButton(
                    text = stringResource(dsR.string.designsystem_action_cancel),
                    onClick = dialogState::hide,
                )
            },
            onDismissRequest = dialogState::hide,
        )
    }
}

@ThemePreviews
@Composable
private fun PostItemPreview() {
    FlowTheme {
        Surface {
            PostItem(
                post = Post(
                    id = "1",
                    author = Author(name = "Author name"),
                    date = "21.12.2020",
                    content = TextContent.Text("Просто-напросто текст"),
                ),
            )
        }
    }
}
