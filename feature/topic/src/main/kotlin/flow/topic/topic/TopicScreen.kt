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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import flow.designsystem.component.AppBarDefaults
import flow.designsystem.component.BackButton
import flow.designsystem.component.ExpandableAppBar
import flow.designsystem.component.FavoriteButton
import flow.designsystem.component.IconButton
import flow.designsystem.component.LazyList
import flow.designsystem.component.Scaffold
import flow.designsystem.component.TextButton
import flow.designsystem.drawables.FlowIcons
import flow.models.topic.Post
import flow.ui.component.Avatar
import flow.ui.component.PageResult
import flow.ui.component.Post
import flow.ui.component.appendItems
import flow.ui.component.dividedItems
import flow.ui.component.emptyItem
import flow.ui.component.errorItem
import flow.ui.component.loadingItem
import kotlinx.coroutines.job
import flow.designsystem.R as DesignsystemR
import flow.ui.R as UiR

@Composable
fun TopicScreen(
    back: () -> Unit,
    openLogin: () -> Unit,
) {
    TopicScreen(
        viewModel = hiltViewModel(),
        onBackClick = back,
        onLoginClick = openLogin,
    )
}

@Composable
private fun TopicScreen(
    viewModel: TopicViewModel,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val onAction: (TopicAction) -> Unit = { action ->
        when (action) {
            is TopicAction.BackClick -> onBackClick()
            is TopicAction.LoginClick -> onLoginClick()
            else -> viewModel.perform(action)
        }
    }
    MobileTopicScreen(state, onAction)
}

@Composable
private fun MobileTopicScreen(
    state: TopicState,
    onAction: (TopicAction) -> Unit,
) {
    val (topic, _, isFavorite) = state.topic
    val scrollBehavior = AppBarDefaults.appBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            var isExpanded by remember { mutableStateOf(false) }
            ExpandableAppBar(
                navigationIcon = { BackButton { onAction(TopicAction.BackClick) } },
                title = {
                    Text(
                        text = topic.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                    )
                },
                actions = {
                    FavoriteButton(
                        isFavorite = isFavorite,
                        onClick = { onAction(TopicAction.FavoriteClick(state.topic)) },
                    )
                    AnimatedVisibility(visible = state.pages > 1) {
                        val rotation by animateFloatAsState(
                            targetValue = if (isExpanded) 180f else 0f
                        )
                        IconButton(
                            modifier = Modifier.rotate(rotation),
                            onClick = { isExpanded = !isExpanded },
                            imageVector = if (isExpanded) {
                                FlowIcons.Collapse
                            } else {
                                FlowIcons.Pages
                            },
                        )
                    }
                },
                expandableContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val isEnabled = state.content !is PageResult.Loading
                        fun goToPage(page: Int) = onAction(TopicAction.GoToPage(page))
                        IconButton(
                            onClick = { goToPage(1) },
                            enabled = isEnabled && state.page != 1,
                            imageVector = FlowIcons.FirstPage,
                        )
                        IconButton(
                            onClick = { goToPage(state.page - 1) },
                            enabled = isEnabled && state.page != 1,
                            imageVector = FlowIcons.PrevPage,
                        )
                        Box(
                            modifier = Modifier.width(48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Crossfade(targetState = isEnabled) { showPage ->
                                if (showPage) {
                                    Text(
                                        text = "${state.page}/${state.pages}",
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                } else {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 1.dp,
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = { goToPage(state.page + 1) },
                            enabled = isEnabled && state.page != state.pages,
                            imageVector = FlowIcons.NextPage,
                        )
                        IconButton(
                            onClick = { goToPage(state.pages) },
                            enabled = isEnabled && state.page != state.pages,
                            imageVector = FlowIcons.LastPage,
                        )
                    }
                },
                isExpanded = isExpanded,
                appBarState = scrollBehavior.state,
            )
        },
        floatingActionButton = {
            var showAddCommentDialog by remember { mutableStateOf(false) }
            FloatingActionButton(
                onClick = { showAddCommentDialog = true },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(imageVector = FlowIcons.Comment, contentDescription = null)
            }
            if (showAddCommentDialog) {
                var textValue by remember { mutableStateOf("") }
                Dialog(onDismissRequest = { showAddCommentDialog = false }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Column {
                            val focusRequester = remember { FocusRequester() }
                            OutlinedTextField(
                                modifier = Modifier
                                    .padding(
                                        start = 24.dp,
                                        top = 16.dp,
                                        end = 24.dp,
                                    )
                                    .focusRequester(focusRequester)
                                    .defaultMinSize(minHeight = 96.dp),
                                value = textValue,
                                onValueChange = { textValue = it },
                                label = { Text("Новый комментарий") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    autoCorrect = true,
                                    imeAction = ImeAction.Done,
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        showAddCommentDialog = false
                                        onAction(TopicAction.AddComment(textValue))
                                    }
                                ),
                                maxLines = 3,
                            )
                            LaunchedEffect(Unit) {
                                coroutineContext.job.invokeOnCompletion { error ->
                                    if (error == null) {
                                        focusRequester.requestFocus()
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(
                                    text = stringResource(DesignsystemR.string.designsystem_action_cancel),
                                    onClick = { showAddCommentDialog = false },
                                )
                                TextButton(
                                    text = stringResource(DesignsystemR.string.designsystem_action_send),
                                    onClick = {
                                        showAddCommentDialog = false
                                        onAction(TopicAction.AddComment(textValue))
                                    },
                                )
                            }
                        }
                    }
                }
            }
        },
    ) { padding ->
        val scrollState = rememberLazyListState()
        val firstVisibleItemIndex by remember {
            derivedStateOf {
                val layoutInfo = scrollState.layoutInfo
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                visibleItemsInfo.firstOrNull()?.index ?: 0
            }
        }
        LaunchedEffect(firstVisibleItemIndex) {
            onAction(TopicAction.FirstVisibleItemIndexChanged(firstVisibleItemIndex))
        }
        LazyList(
            modifier = Modifier.padding(padding),
            state = scrollState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 56.dp),
            onEndOfListReached = { onAction(TopicAction.EndOfListReached) },
        ) {
            when (state.content) {
                is PageResult.Loading -> loadingItem()
                is PageResult.Error -> errorItem(
                    error = state.content.error,
                    onRetryClick = { onAction(TopicAction.RetryClick) },
                )

                is PageResult.Empty -> emptyItem(
                    titleRes = UiR.string.topic_empty_title,
                    subtitleRes = UiR.string.topic_empty_subtitle,
                    imageRes = UiR.drawable.ill_empty,
                )

                is PageResult.Content -> {
                    dividedItems(
                        items = state.content.content,
                        key = Post::id,
                        contentType = { it::class },
                        itemContent = { post -> PostItem(post = post) },
                    )
                    appendItems(
                        state = state.content.append,
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
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Avatar(post.author.avatarUrl)
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = post.author.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                    ),
                )
                Text(
                    text = post.date,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.outline,
                    ),
                )
            }
            Post(
                modifier = Modifier.padding(top = 4.dp),
                content = post.content,
            )
        }
    }
}
