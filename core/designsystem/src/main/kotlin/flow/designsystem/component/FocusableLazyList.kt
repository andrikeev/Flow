package flow.designsystem.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import flow.designsystem.theme.AppTheme
import kotlinx.coroutines.job

@Composable
fun FocusableLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(AppTheme.spaces.zero),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onEndOfListReached: () -> Unit = {},
    refocusFirst: Boolean = true,
    focusableSpec: FocusableSpec = focusableSpec(
        elevation = AppTheme.elevations.small,
        shape = AppTheme.shapes.large,
        color = AppTheme.colors.surface,
    ),
    content: FocusableLazyListScope.() -> Unit,
) {
    val focusRequester = rememberFocusRequester()
    var hasFocus by rememberSaveable { mutableStateOf(false) }
    var lastFocusedIndex by rememberSaveable(content) { mutableStateOf(0) }
    var focusedIndex by rememberSaveable(hasFocus) {
        mutableStateOf(
            when {
                hasFocus && refocusFirst -> 0
                hasFocus && !refocusFirst -> lastFocusedIndex
                else -> -1
            }
        )
    }
    val headerIndexes = remember { mutableListOf<Int>() }
    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            val visibleItems = state.layoutInfo.visibleItemsInfo.map(LazyListItemInfo::index)
            if (focusedIndex !in visibleItems) {
                state.animateScrollToItem(focusedIndex)
                runCatching { focusRequester.requestFocus() }
            } else {
                coroutineContext.job.invokeOnCompletion { error ->
                    if (error == null) {
                        runCatching { focusRequester.requestFocus() }
                    }
                }
            }
        }
    }
    LaunchedEffect(focusedIndex) {
        if (hasFocus) {
            lastFocusedIndex = focusedIndex
        }
        val firstVisibleHeader = state.layoutInfo.visibleItemsInfo.firstOrNull {
            it.index in headerIndexes
        }
        val firstFocusOffset = firstVisibleHeader?.size ?: 0
        val minOffset = state.layoutInfo.viewportStartOffset + firstFocusOffset
        val maxOffset = state.layoutInfo.viewportEndOffset
        val focusedItem = state.layoutInfo.visibleItemsInfo.firstOrNull {
            it.index == focusedIndex
        }
        if (focusedItem != null) {
            val itemSize = focusedItem.size
            val itemOffset = focusedItem.offset
            if (itemOffset < minOffset) {
                state.scrollBy(-itemSize.toFloat())
            }
            if (itemOffset > (maxOffset - itemSize)) {
                state.scrollBy(itemSize.toFloat())
            }
        }
    }
    CompositionLocalProvider(
        LocalFocusRequester provides focusRequester,
        LocalFocusState provides FocusIndexState(
            value = focusedIndex,
            onChanged = { index -> focusedIndex = index },
        ),
    ) {
        LazyList(
            modifier = modifier.onFocusChanged { hasFocus = it.hasFocus },
            state = state,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            onLastItemVisible = onEndOfListReached,
        ) {
            FocusableLazyListScopeImpl(
                lazyListScope = this,
                addHeader = { index -> headerIndexes.add(index) },
                focusableSpec = focusableSpec,
            ).content()
        }
    }
}

@Composable
private fun FocusableItem(
    modifier: Modifier = Modifier,
    focusableSpec: FocusableSpec,
    isFocused: Boolean,
    onFocused: () -> Unit,
    content: @Composable () -> Unit,
) {
    val elevation by animateDpAsState(
        if (isFocused) {
            focusableSpec.elevation
        } else {
            AppTheme.elevations.zero
        }
    )
    val zIndex by animateFloatAsState(
        if (isFocused) {
            focusableSpec.elevation.value
        } else {
            AppTheme.elevations.zero.value
        }
    )
    val itemFocusRequester = if (isFocused) {
        LocalFocusRequester.current
    } else {
        FocusRequester.Default
    }
    val color = if (isFocused) {
        focusableSpec.color
    } else {
        Color.Unspecified
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.sizes.default)
            .padding(AppTheme.spaces.extraSmall)
            .zIndex(zIndex)
            .focusRequester(itemFocusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    onFocused()
                }
            },
        shape = focusableSpec.shape,
        tonalElevation = elevation,
        color = color,
    ) {
        content()
    }
}

private val LocalFocusRequester = compositionLocalOf {
    FocusRequester.Default
}

private val LocalFocusState = compositionLocalOf {
    FocusIndexState(value = 0, onChanged = {})
}

interface FocusableLazyListScope : LazyListScope {
    fun focusableItem(
        key: Any? = null,
        contentType: Any? = null,
        content: @Composable LazyItemScope.() -> Unit,
    )

    fun focusableItems(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit,
    )

    fun focusableStickyHeader(
        key: Any? = null,
        contentType: Any? = null,
        content: @Composable LazyItemScope.() -> Unit,
    )
}

private class FocusableLazyListScopeImpl(
    private val lazyListScope: LazyListScope,
    private val addHeader: (index: Int) -> Unit,
    private val focusableSpec: FocusableSpec,
) : FocusableLazyListScope, LazyListScope by lazyListScope {
    private var focusableItemsCount: Int by mutableStateOf(0)

    override fun item(
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit,
    ) {
        lazyListScope.item(key, contentType) { content() }
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit,
    ) {
        lazyListScope.items(count, key, contentType) { itemContent(it) }
    }

    override fun stickyHeader(
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit,
    ) {
        lazyListScope.stickyHeader(key, contentType) { content() }
    }

    override fun focusableItem(
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit,
    ) {
        val index = focusableItemsCount++
        lazyListScope.item {
            val focusState = LocalFocusState.current
            FocusableItem(
                isFocused = focusState.focusedIndex == index,
                onFocused = { focusState.focusedIndex = index },
                focusableSpec = this@FocusableLazyListScopeImpl.focusableSpec,
            ) {
                content()
            }
        }
    }

    override fun focusableItems(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit,
    ) {
        val startIndex = focusableItemsCount
        lazyListScope.items(
            count = count,
            key = key,
            contentType = contentType,
        ) { index ->
            val focusState = LocalFocusState.current
            FocusableItem(
                isFocused = focusState.focusedIndex == (startIndex + index),
                onFocused = { focusState.focusedIndex = (startIndex + index) },
                focusableSpec = this@FocusableLazyListScopeImpl.focusableSpec,
            ) {
                itemContent(index)
            }
        }
        focusableItemsCount += count
    }

    override fun focusableStickyHeader(
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit,
    ) {
        val index = focusableItemsCount++
        addHeader.invoke(index)
        lazyListScope.stickyHeader {
            val focusState = LocalFocusState.current
            FocusableItem(
                isFocused = focusState.focusedIndex == index,
                onFocused = { focusState.focusedIndex = index },
                focusableSpec = this@FocusableLazyListScopeImpl.focusableSpec,
            ) {
                content()
            }
        }
    }
}

inline fun <T> FocusableLazyListScope.focusableItems(
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline contentType: (item: T) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) = focusableItems(
    count = items.size,
    key = if (key != null) { index: Int -> key(items[index]) } else null,
    contentType = { index: Int -> contentType(items[index]) }
) {
    itemContent(items[it])
}

private class FocusIndexState(
    private val value: Int,
    private val onChanged: (Int) -> Unit,
) {
    var focusedIndex: Int
        get() = value
        set(value) = onChanged(value)
}
