package me.rutrackersearch.app.ui.search.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.common.AppBar
import me.rutrackersearch.app.ui.common.BackButton
import me.rutrackersearch.app.ui.common.ContentElevation
import me.rutrackersearch.app.ui.common.ContentScale
import me.rutrackersearch.app.ui.common.Divider
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.Focusable
import me.rutrackersearch.app.ui.common.FocusableLazyColumn
import me.rutrackersearch.app.ui.common.IconButton
import me.rutrackersearch.app.ui.common.LazyColumn
import me.rutrackersearch.app.ui.common.Page
import me.rutrackersearch.app.ui.common.PageResult
import me.rutrackersearch.app.ui.common.PagesScreen
import me.rutrackersearch.app.ui.common.TextButton
import me.rutrackersearch.app.ui.common.TopicListItem
import me.rutrackersearch.app.ui.common.appendItems
import me.rutrackersearch.app.ui.common.dividedItems
import me.rutrackersearch.app.ui.common.emptyItem
import me.rutrackersearch.app.ui.common.errorItem
import me.rutrackersearch.app.ui.common.focusableItems
import me.rutrackersearch.app.ui.common.focusableSpec
import me.rutrackersearch.app.ui.common.loadingItem
import me.rutrackersearch.app.ui.common.rememberFocusRequester
import me.rutrackersearch.app.ui.common.resId
import me.rutrackersearch.app.ui.forum.forumtree.ForumTreeScreen
import me.rutrackersearch.app.ui.search.result.SearchResultAction.BackClick
import me.rutrackersearch.app.ui.search.result.SearchResultAction.FavoriteClick
import me.rutrackersearch.app.ui.search.result.SearchResultAction.ListBottomReached
import me.rutrackersearch.app.ui.search.result.SearchResultAction.RetryClick
import me.rutrackersearch.app.ui.search.result.SearchResultAction.SearchClick
import me.rutrackersearch.app.ui.search.result.SearchResultAction.SetAuthor
import me.rutrackersearch.app.ui.search.result.SearchResultAction.SetCategories
import me.rutrackersearch.app.ui.search.result.SearchResultAction.SetOrder
import me.rutrackersearch.app.ui.search.result.SearchResultAction.SetPeriod
import me.rutrackersearch.app.ui.search.result.SearchResultAction.SetSort
import me.rutrackersearch.app.ui.search.result.SearchResultAction.TorrentClick
import me.rutrackersearch.app.ui.theme.borders
import me.rutrackersearch.app.ui.theme.surfaceColorAtElevation
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.entity.search.Filter
import me.rutrackersearch.domain.entity.search.Order
import me.rutrackersearch.domain.entity.search.Period
import me.rutrackersearch.domain.entity.search.Sort
import me.rutrackersearch.domain.entity.topic.Author
import me.rutrackersearch.domain.entity.topic.Torrent

@Composable
fun SearchResultScreen(
    onBackClick: () -> Unit,
    onSearchClick: (Filter) -> Unit,
    onTorrentClick: (Torrent) -> Unit,
    onNewFilter: (Filter) -> Unit,
) {
    SearchResultScreen(
        viewModel = hiltViewModel(),
        onBackClick = onBackClick,
        onSearchClick = onSearchClick,
        onTorrentClick = onTorrentClick,
        onNewFilter = onNewFilter,
    )
}

@Composable
private fun SearchResultScreen(
    viewModel: SearchResultViewModel,
    onBackClick: () -> Unit,
    onSearchClick: (Filter) -> Unit,
    onTorrentClick: (Torrent) -> Unit,
    onNewFilter: (Filter) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val onAction: (SearchResultAction) -> Unit = { action ->
        when (action) {
            BackClick -> onBackClick()
            is SearchClick -> onSearchClick(action.filter)
            is TorrentClick -> onTorrentClick(action.torrent)
            is SetPeriod -> onNewFilter(
                state.filter.copy(
                    query = null,
                    period = action.value
                )
            )
            else -> viewModel.perform(action)
        }
    }
    DynamicBox(
        mobileContent = { MobileSearchResultScreen(state, onAction) },
        tvContent = { TVSearchResultScreen(state, onAction) }
    )
}

@Composable
private fun MobileSearchResultScreen(
    state: SearchResultState,
    onAction: (SearchResultAction) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val pinnedScrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
    Scaffold(
        modifier = Modifier.nestedScroll(pinnedScrollBehavior.nestedScrollConnection),
        topBar = {
            SearchAppBar(
                state = state,
                onAction = onAction,
                scrollBehavior = pinnedScrollBehavior,
            )
        },
        floatingActionButton = {
            val showFAB by remember { derivedStateOf { scrollState.firstVisibleItemIndex > 1 } }
            AnimatedVisibility(
                visible = showFAB,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButton(
                    modifier = Modifier.size(40.dp),
                    onClick = { coroutineScope.launch { scrollState.animateScrollToItem(0) } },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Icon(imageVector = Icons.Outlined.ExpandLess, contentDescription = null)
                }
            }
        },
    ) { padding ->
        SearchResultList(
            modifier = Modifier.padding(padding),
            state = state,
            scrollState = scrollState,
            onAction = onAction,
        )
    }
}

@Composable
private fun SearchAppBar(
    state: SearchResultState,
    onAction: (SearchResultAction) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val filter = state.filter
    var isExpanded by remember { mutableStateOf(false) }
    val scrollFraction = scrollBehavior.scrollFraction
    val colors = TopAppBarDefaults.smallTopAppBarColors()
    val containerColor by colors.containerColor(scrollFraction)
    val secondaryContainerColor by animateColorAsState(
        targetValue = if (scrollFraction > 0.01f) {
            MaterialTheme.colorScheme.surfaceColorAtElevation(ContentElevation.large)
        } else {
            MaterialTheme.colorScheme.surfaceColorAtElevation(ContentElevation.small)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        AppBar(
            navigationIcon = { BackButton { onAction(BackClick) } },
            title = {
                SearchTextItem(
                    modifier = Modifier.fillMaxWidth(),
                    filter = filter,
                    onClick = { onAction(SearchClick(filter)) },
                    color = secondaryContainerColor,
                )
            },
            actions = {
                val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
                IconButton(
                    modifier = Modifier.rotate(rotation),
                    onClick = { isExpanded = !isExpanded },
                    imageVector = if (isExpanded) {
                        Icons.Outlined.ExpandLess
                    } else {
                        Icons.Outlined.Tune
                    },
                )
            },
            scrollBehavior = scrollBehavior,
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            Surface(color = containerColor) {
                FilterBar(
                    state = state,
                    onAction = { action ->
                        when (action) {
                            is SetAuthor,
                            is SetCategories,
                            is SetPeriod -> {
                                isExpanded = false
                            }
                            else -> Unit
                        }
                        onAction(action)
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchTextItem(
    modifier: Modifier = Modifier,
    filter: Filter,
    onClick: () -> Unit,
    color: Color,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = color,
    ) {
        filter.query.let { query ->
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                text = if (query.isNullOrBlank()) {
                    stringResource(R.string.search_input_hint)
                } else {
                    query
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (query.isNullOrBlank()) {
                        MaterialTheme.colorScheme.outline
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            )
        }
    }
}

@Composable
private fun FilterBar(
    state: SearchResultState,
    onAction: (SearchResultAction) -> Unit,
) {
    val filter = state.filter
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
        FilterDropdownItem(
            label = stringResource(R.string.filter_sort_label),
            items = Sort.values().toList(),
            selected = filter.sort,
            itemLabel = { sort -> stringResource(sort.resId) },
            onSelected = { sort -> onAction(SetSort(sort)) },
        )
        FilterDropdownItem(
            label = stringResource(R.string.filter_order_label),
            items = Order.values().toList(),
            selected = filter.order,
            itemLabel = { order -> stringResource(order.resId) },
            onSelected = { order -> onAction(SetOrder(order)) },
        )
        FilterDropdownItem(
            label = stringResource(R.string.filter_period_label),
            items = Period.values().toList(),
            selected = filter.period,
            itemLabel = { period ->
                stringResource(period.resId).replaceFirstChar(Char::uppercaseChar)
            },
            onSelected = { period -> onAction(SetPeriod(period)) },
        )
        FilterAuthorItem(
            selected = filter.author,
            onSubmit = { author -> onAction(SetAuthor(author)) },
        )
        FilterCategoryItem(
            available = state.categories,
            selected = filter.categories,
            onSubmit = { categories -> onAction(SetCategories(categories)) },
        )
    }
}


@Composable
private fun SearchResultList(
    modifier: Modifier,
    state: SearchResultState,
    scrollState: LazyListState,
    onAction: (SearchResultAction) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        contentPadding = PaddingValues(top = 8.dp, bottom = 56.dp),
        onEndOfListReached = { onAction(ListBottomReached) },
    ) {
        when (state.content) {
            is PageResult.Loading -> loadingItem()
            is PageResult.Error -> errorItem(
                error = state.content.error,
                onRetryClick = { onAction(RetryClick) },
            )
            is PageResult.Empty -> emptyItem(
                titleRes = R.string.search_result_empty_title,
                subtitleRes = R.string.search_result_empty_subtitle,
                iconRes = R.drawable.ill_empty_search,
            )
            is PageResult.Content -> {
                dividedItems(
                    items = state.content.content,
                    key = { it.data.id },
                    contentType = { it.data::class }
                ) { item ->
                    TopicListItem(
                        topicModel = item,
                        onClick = { onAction(TorrentClick(item.data)) },
                        onFavoriteClick = { onAction(FavoriteClick(item)) },
                    )
                }
                appendItems(
                    state = state.content.append,
                    onRetryClick = { onAction(RetryClick) },
                )
            }
        }
    }
}

@Composable
private fun <T> FilterDropdownItem(
    label: String,
    items: List<T>,
    selected: T,
    itemLabel: @Composable (T) -> String,
    onSelected: (T) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label
        )
        var expanded by remember { mutableStateOf(false) }
        Surface(
            modifier = Modifier.weight(2f),
            shape = MaterialTheme.shapes.small,
            border = MaterialTheme.borders.thin,
            onClick = { expanded = true },
        ) {
            Row(modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = itemLabel(selected),
                )
                Icon(
                    imageVector = if (expanded) {
                        Icons.Outlined.ArrowDropUp
                    } else {
                        Icons.Outlined.ArrowDropDown
                    },
                    contentDescription = null,
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                onSelected(item)
                                expanded = false
                            },
                            text = { Text(itemLabel(item)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterAuthorItem(
    selected: Author?,
    onSubmit: (Author?) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.filter_author_label),
        )
        var showDialog by remember { mutableStateOf(false) }
        Surface(
            modifier = Modifier.weight(2f),
            shape = MaterialTheme.shapes.small,
            border = MaterialTheme.borders.thin,
            onClick = { showDialog = true },
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = selected?.name ?: stringResource(R.string.filter_any),
            )
        }
        if (showDialog) {
            AuthorDialog(
                author = selected,
                onDismissRequest = { showDialog = false },
                onSubmit = onSubmit,
            )
        }
    }
}


@Composable
private fun AuthorDialog(
    author: Author?,
    onDismissRequest: () -> Unit,
    onSubmit: (Author?) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        var textValue by remember { mutableStateOf(author?.name ?: "") }
        fun onSubmit() {
            val newAuthor = textValue
                .takeIf(String::isNotBlank)
                ?.let { Author(name = it) }
            onSubmit(newAuthor)
            onDismissRequest()
        }
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column {
                val focusRequester = remember { FocusRequester() }
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 24.dp, top = 16.dp, end = 24.dp)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text(stringResource(R.string.filter_author_label)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        autoCorrect = true,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { onSubmit() }),
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
                        text = stringResource(R.string.action_cancel),
                        onClick = { onDismissRequest() },
                    )
                    if (author != null) {
                        TextButton(
                            text = stringResource(R.string.action_reset),
                            onClick = {
                                onSubmit(null)
                                onDismissRequest()
                            },
                        )
                    }
                    TextButton(
                        text = stringResource(R.string.action_apply),
                        onClick = { onSubmit() },
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterCategoryItem(
    available: List<Category>,
    selected: List<Category>?,
    onSubmit: (List<Category>?) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.filter_category_label)
        )
        var showDialog by remember { mutableStateOf(false) }
        Surface(
            modifier = Modifier.weight(2f),
            shape = MaterialTheme.shapes.small,
            border = MaterialTheme.borders.thin,
            onClick = { showDialog = true },
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = when {
                    selected == null -> stringResource(R.string.filter_any)
                    selected.size == 1 -> selected.first().name
                    else -> stringResource(R.string.filter_category_counter, selected.size)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (showDialog) {
            CategoriesSelectDialog(
                available = available,
                selected = selected,
                onSubmit = { categories ->
                    onSubmit(categories)
                    showDialog = false
                },
                onDismiss = { showDialog = false },
            )
        }
    }
}

@Composable
private fun CategoriesSelectDialog(
    available: List<Category>,
    selected: List<Category>?,
    onSubmit: (List<Category>?) -> Unit,
    onDismiss: () -> Unit,
) {
    val selectedState = remember { mutableStateListOf(*selected.orEmpty().toTypedArray()) }
    Dialog(onDismissRequest = onDismiss) {
        DynamicBox(
            mobileContent = {
                Surface {
                    Column {
                        PagesScreen(
                            modifier = Modifier.weight(1f),
                            pages = listOf(
                                Page(labelResId = R.string.filter_categories_current) {
                                    AvailableCategoriesList(
                                        available = available,
                                        selectedState = selectedState,
                                    )
                                },
                                Page(labelResId = R.string.filter_categories_all) {
                                    ForumTreeScreen(
                                        selectedState = selectedState,
                                    )
                                },
                            )
                        )
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
                                if (!selected.isNullOrEmpty()) {
                                    TextButton(
                                        text = stringResource(R.string.action_reset),
                                        onClick = { onSubmit(null) },
                                    )
                                }
                                TextButton(
                                    text = stringResource(R.string.action_apply),
                                    onClick = {
                                        onSubmit(selectedState.takeIf(List<Category>::isNotEmpty))
                                    },
                                )
                            }
                        }
                    }
                }
            },
            tvContent = {
                Surface {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            AvailableCategoriesList(
                                available = available.union(selected.orEmpty()).toList(),
                                selectedState = selectedState,
                            )
                        }
                        Surface(tonalElevation = ContentElevation.small) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(
                                    text = stringResource(R.string.action_cancel),
                                    onClick = onDismiss,
                                )
                                if (!selected.isNullOrEmpty()) {
                                    TextButton(
                                        text = stringResource(R.string.action_reset),
                                        onClick = { onSubmit(null) },
                                    )
                                }
                                TextButton(
                                    text = stringResource(R.string.action_apply),
                                    onClick = {
                                        onSubmit(selectedState.takeIf(List<Category>::isNotEmpty))
                                    },
                                )
                            }
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun AvailableCategoriesList(
    available: List<Category>,
    selectedState: SnapshotStateList<Category>,
) {
    DynamicBox(
        mobileContent = {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                itemsIndexed(
                    items = available,
                    key = { _, item -> item.id },
                ) { index, item ->
                    val isSelected = selectedState.contains(item)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        onClick = {
                            if (isSelected) {
                                selectedState.remove(item)
                            } else {
                                selectedState.add(item)
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            Crossfade(targetState = isSelected) { isSelected ->
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckBox,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckBoxOutlineBlank,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                    }
                    if (index < available.lastIndex) {
                        Divider()
                    }
                }
            }
        },
        tvContent = {
            FocusableLazyColumn(
                modifier = Modifier.fillMaxHeight(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.Top,
                focusableSpec = focusableSpec(
                    scale = ContentScale.medium,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                ),
            ) {
                focusableItems(
                    items = available,
                    key = Category::id,
                ) { item ->
                    val isSelected = selectedState.contains(item)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        onClick = {
                            if (isSelected) {
                                selectedState.remove(item)
                            } else {
                                selectedState.add(item)
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            Crossfade(targetState = isSelected) { isSelected ->
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckBox,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckBoxOutlineBlank,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun TVSearchResultScreen(
    state: SearchResultState,
    onAction: (SearchResultAction) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        TVFilterBar(
            modifier = Modifier.weight(1f),
            state = state,
            onAction = onAction,
        )
        TVSearchResultList(
            modifier = Modifier.weight(2f),
            state = state,
            onAction = onAction,
        )
    }
}

@Composable
private fun TVSearchResultList(
    modifier: Modifier = Modifier,
    state: SearchResultState,
    onAction: (SearchResultAction) -> Unit,
) {
    FocusableLazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
        refocusFirst = false,
        onEndOfListReached = { onAction(ListBottomReached) },
    ) {
        when (state.content) {
            is PageResult.Loading -> loadingItem()
            is PageResult.Error -> errorItem(
                error = state.content.error,
                onRetryClick = { onAction(RetryClick) },
            )
            is PageResult.Empty -> emptyItem(
                titleRes = R.string.search_result_empty_title,
                subtitleRes = R.string.search_result_empty_subtitle,
                iconRes = R.drawable.ill_empty_search,
            )
            is PageResult.Content -> {
                focusableItems(
                    items = state.content.content,
                    key = { it.data.id },
                    contentType = { it.data::class },
                ) { item ->
                    TopicListItem(
                        topicModel = item,
                        onClick = { onAction(TorrentClick(item.data)) },
                    )
                }
                appendItems(
                    state = state.content.append,
                    onRetryClick = { onAction(RetryClick) },
                )
            }
        }
    }
}

@Composable
private fun TVFilterBar(
    modifier: Modifier = Modifier,
    state: SearchResultState,
    onAction: (SearchResultAction) -> Unit,
) {
    FocusableLazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
        refocusFirst = false,
    ) {
        focusableItem {
            SearchTextItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                filter = state.filter,
                onClick = { onAction(SearchClick(state.filter)) },
                color = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
        val filter = state.filter
        focusableItem {
            TVFilterDropdownItem(
                label = stringResource(R.string.filter_sort_label),
                items = Sort.values().toList(),
                selected = filter.sort,
                itemLabel = { sort -> stringResource(sort.resId) },
                onSelected = { sort -> onAction(SetSort(sort)) },
            )
        }
        focusableItem {
            TVFilterDropdownItem(
                label = stringResource(R.string.filter_order_label),
                items = Order.values().toList(),
                selected = filter.order,
                itemLabel = { order -> stringResource(order.resId) },
                onSelected = { order -> onAction(SetOrder(order)) },
            )
        }
        focusableItem {
            TVFilterDropdownItem(
                label = stringResource(R.string.filter_period_label),
                items = Period.values().toList(),
                selected = filter.period,
                itemLabel = { period ->
                    stringResource(period.resId).replaceFirstChar(Char::uppercaseChar)
                },
                onSelected = { period -> onAction(SetPeriod(period)) },
            )
        }
        focusableItem {
            TVFilterAuthorItem(
                selected = filter.author,
                onSubmit = { author -> onAction(SetAuthor(author)) },
            )
        }
        focusableItem {
            TVFilterCategoryItem(
                available = state.categories,
                selected = filter.categories,
                onSubmit = { categories -> onAction(SetCategories(categories)) },
            )
        }
    }
}

@Composable
private fun <T> TVFilterDropdownItem(
    label: String,
    items: List<T>,
    selected: T,
    itemLabel: @Composable (T) -> String,
    onSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .height(48.dp),
        shape = MaterialTheme.shapes.small,
        border = MaterialTheme.borders.thin,
        onClick = { expanded = true },
    ) {
        Row(modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)) {
            Text(
                modifier = Modifier.weight(1f),
                text = "$label ${itemLabel(selected).replaceFirstChar(Char::lowercaseChar)}"
            )
            Icon(
                imageVector = if (expanded) {
                    Icons.Outlined.ArrowDropUp
                } else {
                    Icons.Outlined.ArrowDropDown
                },
                contentDescription = null,
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                val focusRequester = rememberFocusRequester()
                LaunchedEffect(expanded) {
                    coroutineContext.job.invokeOnCompletion { error ->
                        if (error == null) {
                            focusRequester.requestFocus()
                        }
                    }
                }
                items.forEachIndexed { index, item ->
                    Focusable(
                        modifier = Modifier.focusRequester(
                            if (index == 0) focusRequester else FocusRequester.Default
                        ),
                        spec = focusableSpec(color = MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                onSelected(item)
                                expanded = false
                            },
                            text = { Text(itemLabel(item)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TVFilterAuthorItem(
    selected: Author?,
    onSubmit: (Author?) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .height(48.dp),
        shape = MaterialTheme.shapes.small,
        border = MaterialTheme.borders.thin,
        onClick = { showDialog = true },
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            text = "${stringResource(R.string.filter_author_label)}: " +
                (selected?.name
                    ?: stringResource(R.string.filter_any).replaceFirstChar(Char::lowercaseChar)),
        )
    }
    if (showDialog) {
        AuthorDialog(
            author = selected,
            onDismissRequest = { showDialog = false },
            onSubmit = onSubmit,
        )
    }
}

@Composable
private fun TVFilterCategoryItem(
    available: List<Category>,
    selected: List<Category>?,
    onSubmit: (List<Category>?) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .height(48.dp),
        shape = MaterialTheme.shapes.small,
        border = MaterialTheme.borders.thin,
        onClick = { showDialog = true },
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            text = stringResource(R.string.filter_category_label) + ": " +
                when {
                    selected == null -> stringResource(R.string.filter_any)
                    selected.size == 1 -> selected.first().name
                    else -> stringResource(R.string.filter_category_counter, selected.size)
                }
        )
    }
    if (showDialog) {
        CategoriesSelectDialog(
            available = available,
            selected = selected,
            onSubmit = { categories ->
                onSubmit(categories)
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}
