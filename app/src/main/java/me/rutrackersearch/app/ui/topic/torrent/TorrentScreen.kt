package me.rutrackersearch.app.ui.topic.torrent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.common.AppBar
import me.rutrackersearch.app.ui.common.BackButton
import me.rutrackersearch.app.ui.common.Button
import me.rutrackersearch.app.ui.common.ContentElevation
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.Error
import me.rutrackersearch.app.ui.common.FavoriteButton
import me.rutrackersearch.app.ui.common.Focusable
import me.rutrackersearch.app.ui.common.IconButton
import me.rutrackersearch.app.ui.common.Loading
import me.rutrackersearch.app.ui.common.LocalSnackbarHostState
import me.rutrackersearch.app.ui.common.Post
import me.rutrackersearch.app.ui.common.TVAppBar
import me.rutrackersearch.app.ui.common.TextButton
import me.rutrackersearch.app.ui.common.TorrentStatus
import me.rutrackersearch.app.ui.common.focusableSpec
import me.rutrackersearch.app.ui.common.rememberTabAppBarScrollBehavior
import me.rutrackersearch.app.ui.platform.LocalOpenLinkHandler
import me.rutrackersearch.app.ui.platform.LocalShareLinkHandler
import me.rutrackersearch.app.ui.theme.TopicColors
import me.rutrackersearch.app.ui.topic.download.DownloadDialog
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.AuthorClick
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.BackClick
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.CategoryClick
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.CommentsClick
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.FavoriteClick
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.MagnetClick
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.RetryClick
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.ShareClick
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.TorrentFileClick
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.Author
import me.rutrackersearch.models.topic.Content
import me.rutrackersearch.models.topic.PostContent
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TorrentDescription
import me.rutrackersearch.models.topic.isValid
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import me.rutrackersearch.app.ui.common.ContentScale as FocusableContentScale

@Composable
fun TorrentScreen(
    back: () -> Unit,
    openLogin: () -> Unit,
    openComments: (Topic) -> Unit,
    openCategory: (Category) -> Unit,
    openSearch: (Filter) -> Unit,
) {
    TorrentScreen(
        viewModel = hiltViewModel(),
        back = back,
        openLogin = openLogin,
        openComments = openComments,
        openCategory = openCategory,
        openSearch = openSearch,
    )
}

@Composable
private fun TorrentScreen(
    viewModel: TorrentViewModel,
    back: () -> Unit,
    openLogin: () -> Unit,
    openComments: (Topic) -> Unit,
    openCategory: (Category) -> Unit,
    openSearch: (Filter) -> Unit,
) {
    val snackbarState = remember { SnackbarHostState() }
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
    CompositionLocalProvider(LocalSnackbarHostState provides snackbarState) {
        DynamicBox(
            mobileContent = { MobileTorrentScreen(state, viewModel::perform) },
            tvContent = { TVTorrentScreen(state, viewModel::perform) },
        )
    }
}

@Composable
private fun MobileTorrentScreen(
    state: TorrentState,
    onAction: (TorrentAction) -> Unit,
) {
    val (torrent, _, isFavorite) = state.torrent
    val (_, title, author, category, _, status, _, _, _, _, magnetLink, description) = torrent
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                navigationIcon = { BackButton { onAction(BackClick) } },
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
                        onClick = { onAction(FavoriteClick) },
                    )
                    IconButton(
                        onClick = { onAction(ShareClick) },
                        imageVector = Icons.Outlined.Share,
                        contentDescription = stringResource(R.string.action_share),
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) },
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
                                onClick = { onAction(CategoryClick(category)) },
                            )
                        }
                        author?.let { author ->
                            Author(
                                author = author,
                                onClick = { onAction(AuthorClick(author)) },
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
                                text = stringResource(R.string.topic_action_magnet),
                                onClick = { magnetLink?.also { onAction(MagnetClick) } },
                                color = TopicColors.magnet,
                            )
                        }
                        item {
                            Button(
                                text = stringResource(R.string.topic_action_torrent),
                                onClick = { onAction(TorrentFileClick) },
                                color = TopicColors.torrent,
                            )
                        }
                    }
                    item {
                        Button(
                            text = stringResource(R.string.topic_action_comments),
                            onClick = { onAction(CommentsClick) },
                            color = TopicColors.comments,
                        )
                    }
                }
            }
            item {
                when {
                    state.isLoading -> Loading()
                    state.error != null -> Error(
                        error = state.error,
                        onRetryClick = { onAction(RetryClick) },
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
private fun TVTorrentScreen(
    state: TorrentState,
    onAction: (TorrentAction) -> Unit,
) {
    val (torrent, _, isFavorite) = state.torrent
    val (_, title, author, category, _, status, _, _, _, _, magnetLink, description) = torrent
    val scrollBehavior = rememberTabAppBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TVAppBar(
                scrollBehavior = scrollBehavior,
                title = title,
                action = {
                    FavoriteButton(
                        isFavorite = isFavorite,
                        onClick = { onAction(FavoriteClick) },
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) },
    ) { padding ->
        Row(
            modifier = Modifier.padding(
                start = 32.dp,
                top = padding.calculateTopPadding(),
                end = 32.dp,
                bottom = padding.calculateBottomPadding(),
            ),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            val scrollState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 16.dp),
                state = scrollState,
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        TorrentImage(
                            modifier = Modifier.weight(1f),
                            torrentDescription = description,
                        )
                        Column(modifier = Modifier.weight(4f)) {
                            TorrentStatus(
                                modifier = Modifier.padding(8.dp),
                                torrent = torrent,
                                horizontalArrangement = Arrangement.Start,
                                itemsPadding = PaddingValues(horizontal = 16.dp),
                            )
                            val focusableSpec = focusableSpec(
                                scale = FocusableContentScale.medium,
                                elevation = ContentElevation.small,
                                shape = MaterialTheme.shapes.extraSmall,
                                color = MaterialTheme.colorScheme.surface,
                            )
                            category?.let { category ->
                                Focusable(spec = focusableSpec) {
                                    Category(
                                        modifier = Modifier.padding(8.dp),
                                        category = category,
                                        onClick = { onAction(CategoryClick(category)) },
                                    )
                                }
                            }
                            author?.let { author ->
                                Focusable(spec = focusableSpec) {
                                    Author(
                                        modifier = Modifier.padding(8.dp),
                                        author = author,
                                        onClick = { onAction(AuthorClick(author)) },
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                if (status.isValid()) {
                                    Button(
                                        text = stringResource(R.string.topic_action_magnet),
                                        onClick = { magnetLink?.also { onAction(MagnetClick) } },
                                        color = TopicColors.magnet,
                                    )
                                    Button(
                                        text = stringResource(R.string.topic_action_torrent),
                                        onClick = { onAction(TorrentFileClick) },
                                        color = TopicColors.torrent,
                                    )
                                }
                                Button(
                                    text = stringResource(R.string.topic_action_comments),
                                    onClick = { onAction(CommentsClick) },
                                    color = TopicColors.comments,
                                )
                            }
                            when {
                                state.isLoading -> Loading()
                                state.error != null -> Error(
                                    error = state.error,
                                    onRetryClick = { onAction(RetryClick) },
                                )
                                description != null -> Post(
                                    modifier = Modifier.padding(8.dp),
                                    content = description.content,
                                )
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 32.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                val coroutineScope = rememberCoroutineScope()
                IconButton(
                    onClick = { coroutineScope.launch { scrollState.scrollBy(-100f) } },
                    imageVector = Icons.Default.KeyboardArrowUp,
                )
                IconButton(
                    onClick = { coroutineScope.launch { scrollState.scrollBy(100f) } },
                    imageVector = Icons.Default.KeyboardArrowDown,
                )
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
        SubcomposeAsyncImage(
            model = src,
            contentDescription = null,
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Success -> Image(
                    painter = painter,
                    contentDescription = null,
                )

                is AsyncImagePainter.State.Loading -> Box(
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

                AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Error -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Outlined.ImageNotSupported,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
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
            text = stringResource(R.string.topic_category_label),
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
            text = stringResource(R.string.topic_author_label),
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
                    Surface(tonalElevation = ContentElevation.small) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(
                                text = stringResource(R.string.action_cancel),
                                onClick = onDismiss,
                            )
                            TextButton(
                                text = stringResource(R.string.action_share),
                                onClick = {
                                    shareLinkHandler.shareLink(link)
                                    onDismiss()
                                },
                            )
                            TextButton(
                                text = stringResource(R.string.action_open),
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
