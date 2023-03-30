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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import flow.designsystem.component.AppBar
import flow.designsystem.component.AppBarDefaults
import flow.designsystem.component.BackButton
import flow.designsystem.component.Button
import flow.designsystem.component.Error
import flow.designsystem.component.FavoriteButton
import flow.designsystem.component.IconButton
import flow.designsystem.component.Loading
import flow.designsystem.component.Scaffold
import flow.designsystem.component.TextButton
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.Elevation
import flow.designsystem.theme.TopicColors
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Author
import flow.models.topic.Content
import flow.models.topic.PostContent
import flow.models.topic.Topic
import flow.models.topic.TorrentDescription
import flow.models.topic.isValid
import flow.topic.download.DownloadDialog
import flow.ui.component.Post
import flow.ui.component.RemoteImage
import flow.ui.component.TorrentStatus
import flow.ui.component.getIllRes
import flow.ui.component.getStringRes
import flow.ui.platform.LocalOpenLinkHandler
import flow.ui.platform.LocalShareLinkHandler
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import flow.designsystem.R as DesignsystemR
import flow.ui.R as UiR

@Composable
internal fun TorrentScreen(
    viewModel: TorrentViewModel,
    back: () -> Unit,
    openLogin: () -> Unit,
    openComments: (Topic) -> Unit,
    openCategory: (Category) -> Unit,
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

            is TorrentSideEffect.OpenCategory -> openCategory(sideEffect.category)
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
                        style = MaterialTheme.typography.titleSmall,
                    )
                },
                actions = {
                    FavoriteButton(
                        isFavorite = isFavorite,
                        onClick = { onAction(TorrentAction.FavoriteClick) },
                    )
                    IconButton(
                        onClick = { onAction(TorrentAction.ShareClick) },
                        imageVector = FlowIcons.Share,
                        contentDescription = stringResource(DesignsystemR.string.designsystem_action_share),
                    )
                },
                appBarState = scrollBehavior.state,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    TorrentImage(
                        modifier = Modifier.weight(1f),
                        torrentDescription = description,
                    )
                    Column(
                        modifier = Modifier.weight(3f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TorrentStatus(
                            modifier = Modifier.fillMaxWidth(),
                            torrent = torrent,
                        )
                        category?.let { category ->
                            Category(
                                category = category,
                                onClick = { onAction(TorrentAction.CategoryClick(category)) },
                            )
                        }
                        author?.let { author ->
                            Author(
                                author = author,
                                onClick = { onAction(TorrentAction.AuthorClick(author)) },
                            )
                        }
                    }
                }
            }
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 16.dp,
                        alignment = Alignment.CenterHorizontally,
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    if (status.isValid()) {
                        item {
                            Button(
                                text = stringResource(UiR.string.topic_action_magnet),
                                onClick = { magnetLink?.also { onAction(TorrentAction.MagnetClick) } },
                                color = TopicColors.magnet,
                            )
                        }
                        item {
                            Button(
                                text = stringResource(UiR.string.topic_action_torrent),
                                onClick = { onAction(TorrentAction.TorrentFileClick) },
                                color = TopicColors.torrent,
                            )
                        }
                    }
                    item {
                        Button(
                            text = stringResource(UiR.string.topic_action_comments),
                            onClick = { onAction(TorrentAction.CommentsClick) },
                            color = TopicColors.comments,
                        )
                    }
                }
            }
            item {
                when {
                    state.isLoading -> Loading()
                    state.error != null -> Error(
                        titleRes = UiR.string.error_title,
                        subtitleRes = state.error.getStringRes(),
                        imageRes = state.error.getIllRes(),
                        onRetryClick = { onAction(TorrentAction.RetryClick) },
                    )

                    description != null -> Post(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp,
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
            .clip(MaterialTheme.shapes.extraSmall),
        contentAlignment = Alignment.TopCenter,
    ) {
        RemoteImage(
            src = src,
            contentDescription = null,
            onLoading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.dp,
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
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = FlowIcons.ImagePlaceholder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
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
        Text(
            text = stringResource(UiR.string.topic_category_label),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable { onClick() },
            text = category.name,
            style = MaterialTheme.typography.titleMedium.copy(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary,
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
        Text(
            text = stringResource(UiR.string.topic_author_label),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable { onClick() },
            text = author.name,
            style = MaterialTheme.typography.titleMedium.copy(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary,
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
        Dialog(onDismissRequest = onDismiss) {
            val openLinkHandler = LocalOpenLinkHandler.current
            val shareLinkHandler = LocalShareLinkHandler.current
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 18.dp),
                    ) {
                        Text(link)
                    }
                    Surface(tonalElevation = Elevation.small) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(
                                text = stringResource(DesignsystemR.string.designsystem_action_cancel),
                                onClick = onDismiss,
                            )
                            TextButton(
                                text = stringResource(DesignsystemR.string.designsystem_action_share),
                                onClick = {
                                    shareLinkHandler.shareLink(link)
                                    onDismiss()
                                },
                            )
                            TextButton(
                                text = stringResource(DesignsystemR.string.designsystem_action_open),
                                onClick = {
                                    openLinkHandler.openLink(link)
                                    onDismiss()
                                },
                            )
                        }
                    }
                }
            }
        }
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
