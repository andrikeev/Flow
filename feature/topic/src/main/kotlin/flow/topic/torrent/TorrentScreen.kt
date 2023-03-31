package flow.topic.torrent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import flow.designsystem.component.AppBar
import flow.designsystem.component.AppBarDefaults
import flow.designsystem.component.BackButton
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Button
import flow.designsystem.component.CircularProgressIndicator
import flow.designsystem.component.Dialog
import flow.designsystem.component.Error
import flow.designsystem.component.FavoriteButton
import flow.designsystem.component.Icon
import flow.designsystem.component.IconButton
import flow.designsystem.component.Loading
import flow.designsystem.component.Scaffold
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.component.ThemePreviews
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Author
import flow.models.topic.Content
import flow.models.topic.PostContent
import flow.models.topic.Topic
import flow.models.topic.TorrentDescription
import flow.models.topic.isValid
import flow.topic.R
import flow.topic.download.DownloadDialog
import flow.ui.component.Post
import flow.ui.component.RemoteImage
import flow.ui.component.TorrentStatus
import flow.ui.component.getIllRes
import flow.ui.component.getStringRes
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
    MobileTorrentScreen(state, viewModel::perform)
}

@Composable
private fun MobileTorrentScreen(
    state: TorrentState,
    onAction: (TorrentAction) -> Unit,
) {
    val (torrent, _, isFavorite) = state.torrent
    val (_, title, author, category, _, status, _, _, _, _, magnetLink, description) = torrent
    val scrollBehavior = AppBarDefaults.appBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                navigationIcon = { BackButton { onAction(TorrentAction.BackClick) } },
                title = {
                    Text(
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = AppTheme.typography.titleSmall,
                    )
                },
                actions = {
                    FavoriteButton(
                        favorite = isFavorite,
                        onClick = { onAction(TorrentAction.FavoriteClick) },
                    )
                    IconButton(
                        icon = FlowIcons.Share,
                        contentDescription = stringResource(dsR.string.designsystem_action_share),
                        onClick = { onAction(TorrentAction.ShareClick) },
                    )
                },
                appBarState = scrollBehavior.state,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(vertical = AppTheme.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spaces.large),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = AppTheme.spaces.large)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spaces.large),
                ) {
                    TorrentImage(
                        modifier = Modifier.weight(1f),
                        torrentDescription = description,
                    )
                    Column(
                        modifier = Modifier.weight(3f),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spaces.medium),
                    ) {
                        TorrentStatus(
                            modifier = Modifier.fillMaxWidth(),
                            torrent = torrent,
                        )
                        category?.let { category ->
                            Category(
                                category = category,
                                onClick = { onAction(TorrentAction.CategoryClick) },
                            )
                        }
                        author?.let { author ->
                            Author(
                                author = author,
                                onClick = { onAction(TorrentAction.AuthorClick) },
                            )
                        }
                    }
                }
            }
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = AppTheme.spaces.large,
                        alignment = Alignment.CenterHorizontally,
                    ),
                    contentPadding = PaddingValues(horizontal = AppTheme.spaces.large),
                ) {
                    if (status.isValid()) {
                        item {
                            Button(
                                text = stringResource(R.string.topic_action_magnet),
                                onClick = { magnetLink?.also { onAction(TorrentAction.MagnetClick) } },
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
            item {
                when {
                    state.isLoading -> Loading()
                    state.error != null -> Error(
                        titleRes = flow.ui.R.string.error_title,
                        subtitleRes = state.error.getStringRes(),
                        imageRes = state.error.getIllRes(),
                        onRetryClick = { onAction(TorrentAction.RetryClick) },
                    )

                    description != null -> Post(
                        modifier = Modifier.padding(
                            horizontal = AppTheme.spaces.large,
                            vertical = AppTheme.spaces.medium,
                        ),
                        content = description.content,
                    )
                }
            }
        }
    }
}

@Composable
private fun TorrentImage(
    modifier: Modifier = Modifier,
    torrentDescription: TorrentDescription?,
) {
    val src = torrentDescription?.content?.torrentImage()?.src
    Box(
        modifier = modifier
            .aspectRatio(2 / 3f)
            .clip(AppTheme.shapes.extraSmall),
        contentAlignment = Alignment.TopCenter,
    ) {
        RemoteImage(
            src = src,
            contentDescription = null,
            onLoading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppTheme.colors.outlineVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppTheme.sizes.medium),
                    )
                }
            },
            onSuccess = { painter ->
                Image(
                    painter = painter,
                    contentDescription = null,
                )
            },
            onError = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppTheme.colors.outlineVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier.size(AppTheme.sizes.default),
                        icon = FlowIcons.ImagePlaceholder,
                        tint = AppTheme.colors.outline,
                        contentDescription = null,
                    )
                }
            },
        )
    }
}

@Composable
private fun Category(
    modifier: Modifier = Modifier,
    category: Category,
    onClick: () -> Unit,
) {
    Row(modifier = modifier) {
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

@Composable
private fun Author(
    modifier: Modifier = Modifier,
    author: Author,
    onClick: () -> Unit,
) {
    Row(modifier = modifier) {
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
