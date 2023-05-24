package flow.topic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flow.designsystem.component.AppBar
import flow.designsystem.component.AppBarState
import flow.designsystem.component.BackButton
import flow.designsystem.component.Body
import flow.designsystem.component.BodySmall
import flow.designsystem.component.Button
import flow.designsystem.component.CircularProgressIndicator
import flow.designsystem.component.Dialog
import flow.designsystem.component.ExpandableAppBar
import flow.designsystem.component.FavoriteButton
import flow.designsystem.component.Icon
import flow.designsystem.component.IconButton
import flow.designsystem.component.LazyList
import flow.designsystem.component.LocalSnackbarHostState
import flow.designsystem.component.ProvideTextStyle
import flow.designsystem.component.Scaffold
import flow.designsystem.component.ScrollBackFloatingActionButton
import flow.designsystem.component.SnackbarHostState
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.component.ThemePreviews
import flow.designsystem.component.rememberVisibilityState
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.models.LoadState
import flow.models.search.Filter
import flow.models.topic.Author
import flow.models.topic.Post
import flow.models.topic.TextContent
import flow.ui.component.Avatar
import flow.ui.component.Post
import flow.ui.component.RemoteImage
import flow.ui.component.TorrentStatus
import flow.ui.component.appendItems
import flow.ui.component.emptyItem
import flow.ui.component.errorItem
import flow.ui.component.loadingItem
import flow.ui.permissions.Permission
import flow.ui.permissions.isGranted
import flow.ui.permissions.rememberPermissionState
import flow.ui.permissions.shouldShowRationale
import flow.ui.platform.LocalOpenFileHandler
import flow.ui.platform.LocalOpenLinkHandler
import flow.ui.platform.LocalShareLinkHandler
import flow.ui.platform.OpenLinkHandler
import flow.ui.platform.ShareLinkHandler
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun TopicScreen(
    viewModel: TopicViewModel,
    back: () -> Unit,
    openCategory: (id: String) -> Unit,
    openLogin: () -> Unit,
    openSearch: (filter: Filter) -> Unit,
) {
    val openLinkHandler = LocalOpenLinkHandler.current
    val shareLinkHandler = LocalShareLinkHandler.current
    val openFileHandler = LocalOpenFileHandler.current
    val magnetDialogState = rememberMagnetDialogState()
    val loginRequestDialogState = rememberVisibilityState()
    val downloadDialogState = rememberVisibilityState()
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TopicSideEffect.Back -> back()
            is TopicSideEffect.OpenCategory -> openCategory(sideEffect.id)
            is TopicSideEffect.OpenFile -> openFileHandler.openFile(sideEffect.uri)
            is TopicSideEffect.OpenLogin -> openLogin()
            is TopicSideEffect.OpenSearch -> openSearch(sideEffect.filter)
            is TopicSideEffect.ShareLink -> shareLinkHandler.shareLink(sideEffect.link)
            is TopicSideEffect.ShowAddCommentDialog -> Unit // TODO
            is TopicSideEffect.ShowAddCommentError -> Unit // TODO
            is TopicSideEffect.ShowDownloadProgress -> downloadDialogState.show()
            is TopicSideEffect.ShowLoginRequired -> loginRequestDialogState.show()
            is TopicSideEffect.ShowMagnet -> magnetDialogState.show(sideEffect.link)
        }
    }

    if (loginRequestDialogState.visible) {
        LoginRequestDialog(
            onDismiss = loginRequestDialogState::hide,
            onLogin = openLogin,
        )
    }

    when (val state = magnetDialogState.state) {
        is MagnetDialogState.Hide -> Unit
        is MagnetDialogState.Show -> MagnetDialog(
            link = state.link,
            onOpenLink = { openLinkHandler.openLink(state.link) },
            onShareLink = { shareLinkHandler.shareLink(state.link) },
            onDismiss = magnetDialogState::hide
        )
    }

    val state by viewModel.collectAsState()
    if (downloadDialogState.visible) {
        DownloadDialog(
            state = state.downloadState,
            onAction = viewModel::perform,
            onDismiss = downloadDialogState::hide,
        )
    }
    TopicScreen(state = state, onAction = viewModel::perform)
}

@Composable
private fun TopicScreen(
    state: TopicState,
    onAction: (TopicAction) -> Unit,
) = Scaffold(
    topBar = { appBarState ->
        TopicAppBar(
            state = state,
            appBarState = appBarState,
            onAction = onAction,
        )
    },
    content = { padding ->
        TopicContent(
            modifier = Modifier.padding(padding),
            state = state,
            onAction = onAction
        )
    },
    floatingActionButton = { ScrollBackFloatingActionButton() },
    bottomBar = {
        Surface(
            tonalElevation = AppTheme.elevations.medium,
            shadowElevation = AppTheme.elevations.medium,
            content = { Pagination(state.paginationState, onAction) }
        )
    },
)

@Composable
private fun TopicAppBar(
    state: TopicState,
    appBarState: AppBarState,
    onAction: (TopicAction) -> Unit,
) {
    when (state.topicContent) {
        is TopicContent.Initial -> AppBar(
            navigationIcon = { BackButton { onAction(TopicAction.BackClick) } },
            appBarState = appBarState,
        )

        is TopicContent.Topic -> TopicAppBar(
            topicContent = state.topicContent,
            favoriteState = state.favoriteState,
            appBarState = appBarState,
            onAction = onAction,
        )

        is TopicContent.Torrent -> TorrentAppBar(
            topicContent = state.topicContent,
            favoriteState = state.favoriteState,
            appBarState = appBarState,
            onAction = onAction,
        )
    }
}

@Composable
private fun TopicAppBar(
    topicContent: TopicContent.Topic,
    favoriteState: TopicFavoriteState,
    appBarState: AppBarState,
    onAction: (TopicAction) -> Unit,
) = AppBar(
    navigationIcon = { BackButton { onAction(TopicAction.BackClick) } },
    title = {
        Text(
            text = topicContent.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = AppTheme.typography.titleSmall,
        )
    },
    actions = {
        FavoriteAction(
            favoriteState = favoriteState,
            onAction = onAction,
        )
    },
    appBarState = appBarState,
)

@Composable
private fun TorrentAppBar(
    topicContent: TopicContent.Torrent,
    favoriteState: TopicFavoriteState,
    appBarState: AppBarState,
    onAction: (TopicAction) -> Unit,
) = ExpandableAppBar(
    navigationIcon = { BackButton { onAction(TopicAction.BackClick) } },
    title = {
        Text(
            text = topicContent.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = AppTheme.typography.titleSmall,
        )
    },
    actions = {
        FavoriteAction(
            favoriteState = favoriteState,
            onAction = onAction,
        )
        IconButton(
            icon = FlowIcons.Share,
            contentDescription = stringResource(flow.designsystem.R.string.designsystem_action_share),
            onClick = { onAction(TopicAction.ShareClick) },
        )
    },
    expandableContent = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.spaces.large),
        ) {
            ProvideTextStyle(value = AppTheme.typography.labelMedium) {
                TorrentStatus(
                    modifier = Modifier
                        .padding(top = AppTheme.spaces.small)
                        .fillMaxWidth()
                        .height(AppTheme.sizes.small),
                    status = topicContent.data.status,
                    size = topicContent.data.size,
                    seeds = topicContent.data.seeds,
                    leeches = topicContent.data.leeches,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppTheme.spaces.large),
            ) {
                val posterDialogState = rememberVisibilityState()
                if (posterDialogState.visible) {
                    TorrentPosterDialog(
                        src = topicContent.data.posterUrl,
                        onDismiss = posterDialogState::hide,
                    )
                }
                Surface(
                    modifier = Modifier.size(AppTheme.sizes.default),
                    shape = AppTheme.shapes.small,
                    onClick = posterDialogState::show,
                ) {
                    RemoteImage(
                        src = topicContent.data.posterUrl,
                        contentDescription = stringResource(R.string.topic_poster_image),
                    )
                }
                topicContent.data.magnetLink.takeIf { !it.isNullOrBlank() }?.let { link ->
                    Spacer(modifier = Modifier.width(AppTheme.spaces.large))
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.topic_action_magnet),
                        onClick = { onAction(TopicAction.MagnetClick(link)) },
                        color = AppTheme.colors.accentRed,
                    )
                    Spacer(modifier = Modifier.width(AppTheme.spaces.large))
                }
                val permission = rememberPermissionState(Permission.WriteExternalStorage)
                val permissionRationaleDialogState = rememberVisibilityState()
                if (permissionRationaleDialogState.visible) {
                    WriteStoragePermissionRationaleDialog(
                        onOk = permission::requestPermission,
                        dismiss = permissionRationaleDialogState::hide,
                    )
                }
                Button(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.topic_action_torrent),
                    onClick = {
                        if (permission.status.isGranted) {
                            onAction(TopicAction.TorrentFileClick(topicContent.title))
                        } else if (permission.status.shouldShowRationale) {
                            permissionRationaleDialogState.show()
                        } else {
                            permission.requestPermission()
                        }
                    },
                    color = AppTheme.colors.accentBlue,
                )
            }
        }
    },
    expanded = true,
    appBarState = appBarState,
)

@Composable
private fun FavoriteAction(
    favoriteState: TopicFavoriteState,
    onAction: (TopicAction) -> Unit,
) = Crossfade(
    targetState = favoriteState,
    label = "TopicFavoriteButton_Crossfade",
) { topicState ->
    when (topicState) {
        is TopicFavoriteState.Initial -> Unit
        is TopicFavoriteState.FavoriteState -> FavoriteButton(
            favorite = topicState.favorite,
            onClick = { onAction(TopicAction.FavoriteClick) },
        )
    }
}

@Composable
private fun Pagination(
    paginationState: PaginationState,
    onAction: (TopicAction) -> Unit,
) = AnimatedVisibility(
    visible = paginationState is PaginationState.Pagination,
    enter = fadeIn() + slideInVertically { it },
    exit = slideOutVertically { it } + fadeOut(),
    label = "TopicPaginationBar_Visibility",
) {
    when (paginationState) {
        is PaginationState.Initial -> Unit
        is PaginationState.NoPagination -> Unit
        is PaginationState.Pagination -> Row(
            modifier = Modifier
                .padding(WindowInsets.Companion.navigationBars.asPaddingValues())
                .fillMaxWidth()
                .height(AppTheme.sizes.default),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            fun goToPage(page: Int) = onAction(TopicAction.GoToPage(page))
            IconButton(
                icon = FlowIcons.FirstPage,
                contentDescription = stringResource(R.string.topic_action_first_page),
                enabled = paginationState.page > 1,
                onClick = { goToPage(1) },
            )
            IconButton(
                icon = FlowIcons.PrevPage,
                contentDescription = stringResource(R.string.topic_action_previous_page),
                enabled = paginationState.page > 1,
                onClick = { goToPage(paginationState.page - 1) },
            )
            Box(
                modifier = Modifier.width(AppTheme.sizes.default),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = buildString {
                        append(paginationState.page)
                        if (paginationState.totalPages > 0) {
                            append('/', paginationState.totalPages)
                        }
                    },
                    style = AppTheme.typography.labelLarge,
                )
            }
            IconButton(
                icon = FlowIcons.NextPage,
                contentDescription = stringResource(R.string.topic_action_next_page),
                enabled = paginationState.page < paginationState.totalPages,
                onClick = { goToPage(paginationState.page + 1) },
            )
            IconButton(
                icon = FlowIcons.LastPage,
                contentDescription = stringResource(R.string.topic_action_last_page),
                enabled = paginationState.page < paginationState.totalPages,
                onClick = { goToPage(paginationState.totalPages) },
            )
        }
    }
}

@Composable
private fun TopicContent(
    state: TopicState,
    modifier: Modifier = Modifier,
    onAction: (TopicAction) -> Unit,
) = LazyList(
    modifier = modifier,
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
            when (val commentsContent = state.commentsContent) {
                is CommentsContent.Empty -> emptyItem(
                    titleRes = R.string.topic_empty_title,
                    subtitleRes = R.string.topic_empty_subtitle,
                    imageRes = flow.ui.R.drawable.ill_empty,
                )

                is CommentsContent.Initial -> loadingItem()
                is CommentsContent.Posts -> {
                    appendItems(
                        state = state.loadStates.prepend,
                        onRetryClick = { onAction(TopicAction.RetryClick) },
                    )
                    items(
                        items = commentsContent.posts,
                        key = Post::id,
                        contentType = { it::class },
                        itemContent = { post -> PostItem(post) },
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
private fun TorrentPosterDialog(
    src: String?,
    onDismiss: () -> Unit,
) = androidx.compose.ui.window.Dialog(
    onDismissRequest = onDismiss,
) {
    Surface(
        tonalElevation = AppTheme.elevations.large,
        shadowElevation = AppTheme.elevations.large,
        shape = AppTheme.shapes.large,
    ) {
        Box(contentAlignment = Alignment.Center) {
            RemoteImage(
                src = src,
                contentDescription = stringResource(R.string.topic_poster_image),
            )
        }
    }
}

@Composable
private fun PostItem(
    post: Post,
    modifier: Modifier = Modifier,
) = Surface(
    modifier = modifier
        .fillMaxWidth()
        .padding(
            horizontal = AppTheme.spaces.mediumLarge,
            vertical = AppTheme.spaces.small,
        ),
    shape = AppTheme.shapes.large,
    tonalElevation = AppTheme.elevations.small,
) {
    Column(modifier = modifier.padding(AppTheme.spaces.mediumLarge)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(AppTheme.sizes.medium)) {
                Avatar(post.author.avatarUrl)
            }
            Column(modifier = Modifier.padding(start = AppTheme.spaces.medium)) {
                Body(
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
            modifier = Modifier.padding(
                start = AppTheme.spaces.small,
                top = AppTheme.spaces.medium,
                end = AppTheme.spaces.small,
            ),
            content = post.content,
        )
    }
}

@Composable
private fun MagnetDialog(
    link: String,
    onOpenLink: () -> Unit,
    onShareLink: () -> Unit,
    onDismiss: () -> Unit,
) = Dialog(
    text = { Text(link) },
    confirmButton = {
        TextButton(
            text = stringResource(flow.designsystem.R.string.designsystem_action_share),
            onClick = onShareLink,
        )
        TextButton(
            text = stringResource(flow.designsystem.R.string.designsystem_action_open),
            onClick = onOpenLink,
        )
    },
    dismissButton = {
        TextButton(
            text = stringResource(flow.designsystem.R.string.designsystem_action_cancel),
            onClick = onDismiss,
        )
    },
    onDismissRequest = onDismiss,
)

@Composable
private fun LoginRequestDialog(
    onDismiss: () -> Unit,
    onLogin: () -> Unit,
) = Dialog(
    icon = { Icon(icon = FlowIcons.Account, contentDescription = null) },
    title = { Text(stringResource(R.string.topics_login_required_title)) },
    text = { Text(stringResource(R.string.topics_login_required_for_download)) },
    confirmButton = {
        TextButton(
            text = stringResource(flow.designsystem.R.string.designsystem_action_login),
            onClick = {
                onLogin()
                onDismiss()
            },
        )
    },
    dismissButton = {
        TextButton(
            text = stringResource(flow.designsystem.R.string.designsystem_action_cancel),
            onClick = onDismiss,
        )
    },
    onDismissRequest = onDismiss,
)

@Composable
private fun WriteStoragePermissionRationaleDialog(
    onOk: () -> Unit,
    dismiss: () -> Unit,
) = Dialog(
    icon = { Icon(icon = FlowIcons.Storage, contentDescription = null) },
    title = { Text(stringResource(R.string.permission_write_storage_rationale_title)) },
    text = { Text(stringResource(R.string.permission_write_storage_rationale)) },
    confirmButton = {
        TextButton(
            text = stringResource(flow.designsystem.R.string.designsystem_action_ok),
            onClick = {
                dismiss()
                onOk()
            },
        )
    },
    dismissButton = {
        TextButton(
            text = stringResource(flow.designsystem.R.string.designsystem_action_cancel),
            onClick = dismiss,
        )
    },
    onDismissRequest = dismiss,
)

@Composable
private fun DownloadDialog(
    state: DownloadState,
    onAction: (TopicAction) -> Unit,
    onDismiss: () -> Unit,
) {
    when (state) {
        is DownloadState.Completed -> Dialog(
            icon = {
                Icon(
                    icon = FlowIcons.FileDownloadDone,
                    contentDescription = null
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.topic_file_download_completed),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    text = stringResource(flow.designsystem.R.string.designsystem_action_open_file),
                    onClick = {
                        onDismiss()
                        onAction(TopicAction.OpenFileClick(state.uri))
                    },
                )
            },
            dismissButton = {
                TextButton(
                    text = stringResource(flow.designsystem.R.string.designsystem_action_cancel),
                    onClick = onDismiss,
                )
            },
            onDismissRequest = onDismiss,
        )

        is DownloadState.Error -> Dialog(
            icon = {
                Icon(
                    icon = FlowIcons.Clear,
                    contentDescription = null
                )
            },
            title = {
                Text(
                    text = stringResource(flow.ui.R.string.error_title),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    text = stringResource(flow.designsystem.R.string.designsystem_action_close),
                    onClick = onDismiss,
                )
            },
            onDismissRequest = onDismiss,
        )

        is DownloadState.Initial,
        is DownloadState.Started -> Dialog(
            icon = {
                CircularProgressIndicator(
                    modifier = Modifier.size(AppTheme.sizes.mediumSmall),
                    strokeWidth = 3.dp,
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.topic_file_download_in_progress),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    text = stringResource(flow.designsystem.R.string.designsystem_action_close),
                    onClick = onDismiss,
                )
            },
            onDismissRequest = onDismiss,
        )
    }
}

@Stable
private class MagnetDialogState {
    var state: State by mutableStateOf(Hide)
        private set

    fun show(link: String) {
        state = Show(link)
    }

    fun hide() {
        state = Hide
    }

    sealed interface State
    object Hide : State
    data class Show(val link: String) : State
}

@Composable
private fun rememberMagnetDialogState() = remember { MagnetDialogState() }

@ThemePreviews
@Composable
private fun MagnetDialogPreview() {
    FlowTheme {
        CompositionLocalProvider(
            LocalOpenLinkHandler provides object : OpenLinkHandler {
                override fun openLink(link: String) = Unit
            },
            LocalShareLinkHandler provides object : ShareLinkHandler {
                override fun shareLink(link: String) = Unit
            },
        ) {
            MagnetDialog(link = "magnet://test", {}, {}, {})
        }
    }
}

@ThemePreviews
@Composable
private fun DownloadDialogPreview() {
    FlowTheme {
        DownloadDialog(DownloadState.Completed(""), {}, {})
    }
}

@ThemePreviews
@Composable
private fun PostItemPreview() {
    FlowTheme {
        CompositionLocalProvider(
            LocalOpenLinkHandler provides OpenLinkHandler.Companion.Stub,
            LocalSnackbarHostState provides SnackbarHostState.Companion.Stub,
        ) {
            PostItem(
                post = Post(
                    id = "1",
                    author = Author("1", "Author name"),
                    date = "Today",
                    content = TextContent.Text("Hi everyone!"),
                )
            )
        }
    }
}