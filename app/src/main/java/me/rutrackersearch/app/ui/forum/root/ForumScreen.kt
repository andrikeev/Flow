package me.rutrackersearch.app.ui.forum.root

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.common.CategoryListItem
import me.rutrackersearch.app.ui.common.ContentElevation
import me.rutrackersearch.app.ui.common.Divider
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.Error
import me.rutrackersearch.app.ui.common.FocusableLazyColumn
import me.rutrackersearch.app.ui.common.Loading
import me.rutrackersearch.app.ui.common.dividedItems
import me.rutrackersearch.app.ui.common.focusableItems
import me.rutrackersearch.app.ui.common.rememberFocusRequester
import me.rutrackersearch.app.ui.forum.root.ForumAction.CategoryClick
import me.rutrackersearch.app.ui.forum.root.ForumAction.ExpandClick
import me.rutrackersearch.app.ui.forum.root.ForumAction.RetryClick
import me.rutrackersearch.app.ui.theme.surfaceColorAtElevation
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.RootCategory

@Composable
fun ForumScreen(
    onCategoryClick: (Category) -> Unit,
) {
    ForumScreen(
        viewModel = hiltViewModel(),
        onCategoryClick = onCategoryClick,
    )
}

@Composable
private fun ForumScreen(
    viewModel: ForumViewModel,
    onCategoryClick: (Category) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    ForumScreen(state) { action ->
        when (action) {
            is CategoryClick -> onCategoryClick(action.category)
            else -> viewModel.perform(action)
        }
    }
}

@Composable
private fun ForumScreen(
    state: ForumState,
    onAction: (ForumAction) -> Unit,
) {
    when (state) {
        is ForumState.Loading -> Loading()
        is ForumState.Error -> Error(
            error = state.error,
            onRetryClick = { onAction(RetryClick) },
        )
        is ForumState.Loaded -> DynamicBox(
            mobileContent = { MobileForumList(state.forum, onAction) },
            tvContent = { TVForumList(state.forum, onAction) },
        )
    }
}

@Composable
private fun MobileForumList(
    forum: List<Expandable<RootCategory>>,
    onAction: (ForumAction) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        dividedItems(
            items = forum,
            key = { it.item.name },
        ) { item ->
            RootCategory(
                rootCategory = item.item,
                isExpanded = item.expanded,
                onCategoryClick = { category -> onAction(CategoryClick(category)) },
                onExpandClick = { onAction(ExpandClick(item)) }
            )
        }
    }
}

@Composable
private fun TVForumList(
    forum: List<Expandable<RootCategory>>,
    onAction: (ForumAction) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        var selectedForumIndex by rememberSaveable { mutableStateOf(0) }
        TVRootCategoriesList(
            modifier = Modifier.weight(1f),
            items = forum.map(Expandable<RootCategory>::item),
            selectedForumIndex = selectedForumIndex,
            onSelectForum = { selectedForumIndex = it },
        )
        TVCategoriesList(
            modifier = Modifier.weight(1f),
            items = forum[selectedForumIndex].item.children,
            onCategoryClick = { onAction(CategoryClick(it)) },
        )
    }
}

@Composable
fun TVRootCategoriesList(
    modifier: Modifier = Modifier,
    items: List<RootCategory>,
    selectedForumIndex: Int,
    onSelectForum: (Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val focusRequester = rememberFocusRequester()
    var hasFocus by rememberSaveable { mutableStateOf(false) }
    var focusedItemIndex by remember(hasFocus) {
        mutableStateOf(if (hasFocus) selectedForumIndex else -1)
    }
    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            val visibleItems = scrollState.layoutInfo.visibleItemsInfo.map(LazyListItemInfo::index)
            if (selectedForumIndex !in visibleItems) {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(selectedForumIndex)
                    focusRequester.requestFocus()
                }
            } else {
                coroutineContext.job.invokeOnCompletion { error ->
                    if (error == null) {
                        focusRequester.requestFocus()
                    }
                }
            }
        }
    }
    LaunchedEffect(focusedItemIndex) {
        val minOffset = scrollState.layoutInfo.viewportStartOffset
        val maxOffset = scrollState.layoutInfo.viewportEndOffset
        val focusedItem = scrollState.layoutInfo.visibleItemsInfo.firstOrNull {
            it.index == focusedItemIndex
        }
        if (focusedItem != null) {
            val itemSize = focusedItem.size
            val itemOffset = focusedItem.offset
            if (itemOffset < minOffset) {
                coroutineScope.launch {
                    scrollState.scrollBy(-itemSize.toFloat())
                }
            }
            if (itemOffset > (maxOffset - itemSize)) {
                coroutineScope.launch {
                    scrollState.scrollBy(itemSize.toFloat())
                }
            }
        }
    }
    LazyColumn(
        modifier = modifier
            .onFocusChanged { hasFocus = it.hasFocus }
            .fillMaxSize(),
        contentPadding = PaddingValues(
            start = 32.dp,
            top = 32.dp,
            end = 16.dp,
            bottom = 32.dp,
        ),
        state = scrollState,
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.hashCode() },
        ) { index, item ->
            val isFocused = focusedItemIndex == index
            val isSelected = selectedForumIndex == index
            val scale by animateFloatAsState(if (isFocused) 1.02f else 1f)
            val elevation by animateDpAsState(
                if (isFocused || isSelected) {
                    ContentElevation.small
                } else {
                    ContentElevation.zero
                }
            )
            val containerColor by animateColorAsState(
                if (isSelected) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surfaceColorAtElevation(elevation)
                }
            )
            val contentColor by animateColorAsState(
                if (isSelected) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            val itemFocusRequester = if (isSelected) {
                focusRequester
            } else {
                FocusRequester.Default
            }
            Surface(
                modifier = Modifier
                    .padding(2.dp)
                    .focusRequester(itemFocusRequester)
                    .heightIn(min = 48.dp)
                    .scale(scale)
                    .onFocusChanged {
                        if (it.isFocused) {
                            focusedItemIndex = index
                        }
                    },
                color = containerColor,
                shape = MaterialTheme.shapes.large,
                contentColor = contentColor,
                onClick = { onSelectForum(index) },
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TVCategoriesList(
    modifier: Modifier = Modifier,
    items: List<Category>,
    onCategoryClick: (Category) -> Unit,
) {
    FocusableLazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 32.dp, end = 32.dp, bottom = 32.dp),
        refocusFirst = false,
    ) {
        focusableItems(items) { category ->
            Surface(onClick = { onCategoryClick(category) }) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun RootCategory(
    rootCategory: RootCategory,
    isExpanded: Boolean,
    onCategoryClick: (Category) -> Unit,
    onExpandClick: () -> Unit,
) {
    val containerColor by animateColorAsState(
        with(MaterialTheme.colorScheme) {
            if (isExpanded) surfaceVariant else surface
        }
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = containerColor,
        onClick = onExpandClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                text = rootCategory.name,
                style = MaterialTheme.typography.bodyMedium,
            )
            val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
            Icon(
                modifier = Modifier.rotate(rotation),
                imageVector = Icons.Outlined.ExpandMore,
                contentDescription = null,
            )
        }
    }
    AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            rootCategory.children.forEachIndexed { index, category ->
                CategoryListItem(
                    modifier = Modifier.padding(start = 24.dp),
                    category = category,
                    onClick = { onCategoryClick(category) }
                )
                if (index < rootCategory.children.lastIndex) {
                    Divider(startIndent = 24.dp)
                }
            }
        }
    }
}
