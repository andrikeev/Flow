package flow.topic.torrent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import flow.designsystem.component.BackButton
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Button
import flow.designsystem.component.CollapsingAppBar
import flow.designsystem.component.CollapsingAppBarState
import flow.designsystem.component.Dialog
import flow.designsystem.component.FavoriteButton
import flow.designsystem.component.IconButton
import flow.designsystem.component.ProvideTextStyle
import flow.designsystem.component.Scaffold
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.component.ThemePreviews
import flow.designsystem.component.rememberCollapsingAppBarBehavior
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Author
import flow.models.topic.Content
import flow.models.topic.PostContent
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.models.topic.TorrentDescription
import flow.models.topic.isValid
import flow.topic.R
import flow.topic.download.DownloadDialog
import flow.ui.component.Post
import flow.ui.component.RemoteImage
import flow.ui.component.TorrentStatus
import flow.ui.component.emptyItem
import flow.ui.component.errorItem
import flow.ui.component.loadingItem
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
    back: () -> Unit,
    openLogin: () -> Unit,
    openComments: (Topic) -> Unit,
    openCategory: (String) -> Unit,
    openSearch: (Filter) -> Unit,
) {
    val shareLinkHandler = LocalShareLinkHandler.current
    var magnetLinkDialogState by remember {
        mutableStateOf<MagnetLinkDialogState>(MagnetLinkDialogState.Hide)
    }
    var torrentFileDialogState by remember {
        mutableStateOf<TorrentFileDialogState>(TorrentFileDialogState.Hide)
    }
    MagnetDialog(
        state = magnetLinkDialogState,
        onDismiss = { magnetLinkDialogState = MagnetLinkDialogState.Hide }
    )
    TorrentFileDialog(
        state = torrentFileDialogState,
        onDismiss = { torrentFileDialogState = TorrentFileDialogState.Hide },
        onLogin = openLogin,
    )
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TorrentSideEffect.Back -> back()
            is TorrentSideEffect.Download -> {
                torrentFileDialogState = TorrentFileDialogState.Show
            }

            is TorrentSideEffect.OpenCategory -> openCategory(sideEffect.categoryId)
            is TorrentSideEffect.OpenComments -> openComments(sideEffect.topic)
            is TorrentSideEffect.OpenMagnet -> {
                magnetLinkDialogState = MagnetLinkDialogState.Show(sideEffect.magnetLink)
            }

            is TorrentSideEffect.OpenSearch -> openSearch(sideEffect.filter)
            is TorrentSideEffect.Share -> shareLinkHandler.shareLink(sideEffect.link)
        }
    }
    val state by viewModel.collectAsState()
    TorrentScreen(state, viewModel::perform)
}

@Composable
private fun TorrentScreen(
    state: TorrentScreenState,
    onAction: (TorrentAction) -> Unit,
) {
    val appBarBehavior = rememberCollapsingAppBarBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(appBarBehavior.nesterScrollConnection),
        topBar = {
            TorrentAppBar(
                torrent = state.torrentState.torrent,
                favoriteState = state.favoriteState,
                appBarState = appBarBehavior.collapsingAppBarState,
                onAction = onAction,
            )
        },
        content = { padding ->
            TorrentContent(
                modifier = Modifier.padding(padding),
                state = state.torrentState,
                onAction = onAction,
            )
        },
    )
}

@Composable
private fun TorrentAppBar(
    torrent: Torrent,
    favoriteState: TorrentFavoriteState,
    appBarState: CollapsingAppBarState,
    onAction: (TorrentAction) -> Unit,
) = CollapsingAppBar(
        backgroundImage = {
            RemoteImage(
                src = torrent.description?.content?.torrentImage()?.src,
                modifier = Modifier.fillMaxWidth(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        },
        navigationIcon = { BackButton { onAction(TorrentAction.BackClick) } },
        title = {
            Text(
                text = torrent.title,
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
                        torrent = torrent,
                    )
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    contentPadding = PaddingValues(AppTheme.spaces.large),
                ) {
                    if (torrent.status.isValid()) {
                        item {
                            Button(
                                text = stringResource(R.string.topic_action_magnet),
                                onClick = { onAction(TorrentAction.MagnetClick) },
                                color = AppTheme.colors.accentRed,
                            )
                        }
                        item {
                            Button(
                                text = stringResource(R.string.topic_action_torrent),
                                onClick = { onAction(TorrentAction.TorrentFileClick) },
                                color = AppTheme.colors.accentBlue,
                            )
                        }
                    }
                    item {
                        Button(
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
    category(state.torrent.category) {
        onAction(TorrentAction.CategoryClick)
    }
    author(state.torrent.author) {
        onAction(TorrentAction.AuthorClick)
    }
    when (state) {
        is TorrentState.Error -> errorItem(
            error = RuntimeException(),
            fillParentMaxSize = false,
            onRetryClick = { onAction(TorrentAction.RetryClick) },
        )

        is TorrentState.Initial -> loadingItem(fillParentMaxSize = false)
        is TorrentState.Loaded -> description(state.torrent.description)
    }
}

private fun LazyListScope.category(
    category: Category?,
    onClick: () -> Unit,
) {
    if (category != null) {
        item {
            Row(modifier = Modifier.padding(horizontal = AppTheme.spaces.large)) {
                BodyLarge(text = stringResource(R.string.topic_category_label))
                Text(
                    modifier = Modifier
                        .padding(start = AppTheme.spaces.medium)
                        .clickable { onClick() },
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
    onClick: () -> Unit,
) {
    if (author != null) {
        item {
            Row(modifier = Modifier.padding(horizontal = AppTheme.spaces.large)) {
                BodyLarge(text = stringResource(R.string.topic_author_label))
                Text(
                    modifier = Modifier
                        .padding(start = AppTheme.spaces.medium)
                        .clickable { onClick() },
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
    state: MagnetLinkDialogState,
    onDismiss: () -> Unit,
) {
    if (state is MagnetLinkDialogState.Show) {
        val link = state.link
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
}

@Composable
private fun TorrentFileDialog(
    state: TorrentFileDialogState,
    onDismiss: () -> Unit,
    onLogin: () -> Unit,
) {
    if (state == TorrentFileDialogState.Show) {
        DownloadDialog(
            dismiss = onDismiss,
            openLogin = onLogin,
        )
    }
}


private sealed interface MagnetLinkDialogState {
    object Hide : MagnetLinkDialogState
    data class Show(val link: String) : MagnetLinkDialogState
}

private sealed interface TorrentFileDialogState {
    object Hide : TorrentFileDialogState
    object Show : TorrentFileDialogState
}

private fun Content.torrentImage(): PostContent.TorrentMainImage? {
    return when (this) {
        is PostContent.TorrentMainImage -> this
        is PostContent.Default -> children.firstNotNullOfOrNull { it.torrentImage() }
        else -> null
    }
}

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
            MagnetDialog(state = MagnetLinkDialogState.Show("magnet://test")) {}
        }
    }
}
