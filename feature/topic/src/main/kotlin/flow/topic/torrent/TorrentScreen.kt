package flow.topic.torrent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flow.designsystem.component.BackButton
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Button
import flow.designsystem.component.CircularProgressIndicator
import flow.designsystem.component.CollapsingAppBar
import flow.designsystem.component.CollapsingAppBarState
import flow.designsystem.component.Dialog
import flow.designsystem.component.FavoriteButton
import flow.designsystem.component.Icon
import flow.designsystem.component.IconButton
import flow.designsystem.component.ProvideTextStyle
import flow.designsystem.component.Scaffold
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.component.ThemePreviews
import flow.designsystem.component.rememberCollapsingAppBarBehavior
import flow.designsystem.component.rememberDialogState
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Author
import flow.models.topic.TorrentDescription
import flow.topic.R
import flow.ui.component.Post
import flow.ui.component.RemoteImage
import flow.ui.component.TorrentStatus
import flow.ui.component.emptyItem
import flow.ui.platform.LocalOpenFileHandler
import flow.ui.platform.LocalOpenLinkHandler
import flow.ui.platform.LocalShareLinkHandler
import flow.ui.platform.OpenLinkHandler
import flow.ui.platform.ShareLinkHandler
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import flow.designsystem.R as dsR

@Composable
internal fun TorrentScreen(
    viewModel: TorrentViewModel,
    torrentState: TorrentState,
    back: () -> Unit,
    openCategory: (id: String) -> Unit,
    openComments: (id: String) -> Unit,
    openLogin: () -> Unit,
    openSearch: (filter: Filter) -> Unit,
) {
    val shareLinkHandler = LocalShareLinkHandler.current
    val openFileHandler = LocalOpenFileHandler.current
    val magnetDialogState = rememberMagnetDialogState()
    val loginRequestDialogState = rememberDialogState()
    val downloadDialogState = rememberDialogState()
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TorrentSideEffect.Back -> back()
            is TorrentSideEffect.OpenCategory -> openCategory(sideEffect.id)
            is TorrentSideEffect.OpenComments -> openComments(sideEffect.id)
            is TorrentSideEffect.OpenFile -> openFileHandler.openFile(sideEffect.uri)
            is TorrentSideEffect.OpenLogin -> openLogin()
            is TorrentSideEffect.OpenSearch -> openSearch(sideEffect.filter)
            is TorrentSideEffect.ShareLink -> shareLinkHandler.shareLink(sideEffect.link)
            is TorrentSideEffect.ShowDownloadProgress -> downloadDialogState.show()
            is TorrentSideEffect.ShowLoginRequest -> loginRequestDialogState.show()
            is TorrentSideEffect.ShowMagnet -> magnetDialogState.show(sideEffect.link)
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
    TorrentScreen(torrentState, state.favoriteState, viewModel::perform)
}

@Composable
private fun TorrentScreen(
    state: TorrentState,
    favoriteState: TorrentFavoriteState,
    onAction: (TorrentAction) -> Unit,
) {
    val appBarBehavior = rememberCollapsingAppBarBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(appBarBehavior.nesterScrollConnection),
        topBar = {
            TorrentAppBar(
                state = state,
                favoriteState = favoriteState,
                appBarState = appBarBehavior.collapsingAppBarState,
                onAction = onAction,
            )
        },
        content = { padding ->
            TorrentContent(
                modifier = Modifier.padding(padding),
                state = state,
                onAction = onAction,
            )
        },
    )
}

@Composable
private fun TorrentAppBar(
    state: TorrentState,
    favoriteState: TorrentFavoriteState,
    appBarState: CollapsingAppBarState,
    onAction: (TorrentAction) -> Unit,
) = CollapsingAppBar(
    backgroundImage = {
        RemoteImage(
            src = state.posterImage,
            modifier = Modifier.fillMaxWidth(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    },
    navigationIcon = { BackButton { onAction(TorrentAction.BackClick) } },
    title = {
        Text(
            text = state.title,
            overflow = TextOverflow.Ellipsis,
        )
    },
    actions = {
        when (favoriteState) {
            is TorrentFavoriteState.FavoriteState -> {
                FavoriteButton(
                    favorite = favoriteState.favorite,
                    onClick = { onAction(TorrentAction.FavoriteClick) },
                )
            }

            is TorrentFavoriteState.Initial -> Unit
        }
        IconButton(
            icon = FlowIcons.Share,
            contentDescription = stringResource(dsR.string.designsystem_action_share),
            onClick = { onAction(TorrentAction.ShareClick) },
        )
    },
    additionalContent = {
        Column {
            ProvideTextStyle(value = AppTheme.typography.labelMedium) {
                TorrentStatus(
                    modifier = Modifier
                        .padding(top = AppTheme.spaces.small)
                        .fillMaxWidth()
                        .height(AppTheme.sizes.small),
                    status = state.status,
                    date = state.date,
                    size = state.size,
                    seeds = state.seeds,
                    leeches = state.leeches,
                )
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (state.showMagnetLink && !state.magnetLink.isNullOrBlank()) {
                    item {
                        Button(
                            modifier = Modifier.padding(
                                vertical = AppTheme.spaces.large,
                                horizontal = AppTheme.spaces.small,
                            ),
                            text = stringResource(R.string.topic_action_magnet),
                            onClick = { onAction(TorrentAction.MagnetClick(state.magnetLink)) },
                            color = AppTheme.colors.accentRed,
                        )
                    }
                }
                if (state.showTorrentFile) {
                    item {
                        Button(
                            modifier = Modifier.padding(
                                vertical = AppTheme.spaces.large,
                                horizontal = AppTheme.spaces.small,
                            ),
                            text = stringResource(R.string.topic_action_torrent),
                            onClick = { onAction(TorrentAction.TorrentFileClick(state.title)) },
                            color = AppTheme.colors.accentBlue,
                        )
                    }
                }
                item {
                    Button(
                        modifier = Modifier.padding(
                            vertical = AppTheme.spaces.large,
                            horizontal = AppTheme.spaces.small,
                        ),
                        text = stringResource(R.string.topic_action_comments),
                        onClick = { onAction(TorrentAction.CommentsClick) },
                        color = AppTheme.colors.accentOrange,
                    )
                }
            }
        }
    },
    appBarState = appBarState,
)

@Composable
private fun TorrentContent(
    state: TorrentState,
    modifier: Modifier = Modifier,
    onAction: (TorrentAction) -> Unit,
) = LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(vertical = AppTheme.spaces.large),
    verticalArrangement = Arrangement.spacedBy(AppTheme.spaces.large),
) {
    category(state.category, onAction)
    author(state.author, onAction)
    description(state.description)
}

private fun LazyListScope.category(
    category: Category?,
    onAction: (TorrentAction) -> Unit,
) {
    if (category != null) {
        item {
            Row(modifier = Modifier.padding(horizontal = AppTheme.spaces.large)) {
                BodyLarge(text = stringResource(R.string.topic_category_label))
                Text(
                    modifier = Modifier
                        .padding(start = AppTheme.spaces.medium)
                        .clickable { onAction(TorrentAction.CategoryClick(category)) },
                    text = category.name,
                    style = AppTheme.typography.titleMedium.copy(
                        textDecoration = TextDecoration.Underline,
                        color = AppTheme.colors.primary,
                    ),
                )
            }
        }
    }
}

private fun LazyListScope.author(
    author: Author?,
    onAction: (TorrentAction) -> Unit,
) {
    if (author != null) {
        item {
            Row(modifier = Modifier.padding(horizontal = AppTheme.spaces.large)) {
                BodyLarge(text = stringResource(R.string.topic_author_label))
                Text(
                    modifier = Modifier
                        .padding(start = AppTheme.spaces.medium)
                        .clickable { onAction(TorrentAction.AuthorClick(author)) },
                    text = author.name,
                    style = AppTheme.typography.titleMedium.copy(
                        textDecoration = TextDecoration.Underline,
                        color = AppTheme.colors.primary,
                    ),
                )
            }
        }
    }
}

private fun LazyListScope.description(
    description: TorrentDescription?,
) {
    if (description != null) {
        item {
            Post(
                modifier = Modifier.padding(
                    horizontal = AppTheme.spaces.large,
                    vertical = AppTheme.spaces.medium,
                ),
                content = description.content,
            )
        }
    } else {
        emptyItem(
            titleRes = R.string.topic_empty_title,
            subtitleRes = R.string.topic_empty_subtitle,
            imageRes = flow.ui.R.drawable.ill_empty,
            fillParentMaxSize = false,
        )
    }
}

@Composable
private fun MagnetDialog(
    link: String,
    onDismiss: () -> Unit,
) {
    val openLinkHandler = LocalOpenLinkHandler.current
    val shareLinkHandler = LocalShareLinkHandler.current
    Dialog(
        text = { Text(link) },
        confirmButton = {
            TextButton(
                text = stringResource(dsR.string.designsystem_action_share),
                onClick = {
                    shareLinkHandler.shareLink(link)
                    onDismiss()
                },
            )
            TextButton(
                text = stringResource(dsR.string.designsystem_action_open),
                onClick = {
                    openLinkHandler.openLink(link)
                    onDismiss()
                },
            )
        },
        dismissButton = {
            TextButton(
                text = stringResource(dsR.string.designsystem_action_cancel),
                onClick = onDismiss,
            )
        },
        onDismissRequest = onDismiss,
    )
}

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
private fun DownloadDialog(
    state: DownloadState,
    onAction: (TorrentAction) -> Unit,
    onDismiss: () -> Unit,
) = Dialog(
    icon = {
        when (state) {
            is DownloadState.Completed -> Icon(icon = FlowIcons.FileDownloadDone, contentDescription = null)
            is DownloadState.Error -> Icon(icon = FlowIcons.Clear, contentDescription = null)
            is DownloadState.Initial,
            is DownloadState.Started -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(AppTheme.sizes.mediumSmall),
                    strokeWidth = 3.dp,
                )
            }
        }
    },
    title = {
        Text(
            text = stringResource(
                when (state) {
                    is DownloadState.Completed -> R.string.topic_file_download_completed
                    is DownloadState.Error -> flow.ui.R.string.error_title
                    is DownloadState.Initial,
                    is DownloadState.Started -> R.string.topic_file_download_in_progress
                }
            ),
            textAlign = TextAlign.Center
        )
    },
    confirmButton = {
        when (state) {
            is DownloadState.Completed -> {
                TextButton(
                    text = stringResource(dsR.string.designsystem_action_open_file),
                    onClick = {
                        onDismiss()
                        onAction(TorrentAction.OpenFileClick(state.uri))
                    },
                )
            }

            DownloadState.Error -> Unit
            DownloadState.Initial -> Unit
            DownloadState.Started -> Unit
        }
    },
    dismissButton = {
        TextButton(
            text = stringResource(dsR.string.designsystem_action_cancel),
            onClick = onDismiss,
        )
    },
    onDismissRequest = onDismiss,
)

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
            MagnetDialog(link = "magnet://test") {}
        }
    }
}
