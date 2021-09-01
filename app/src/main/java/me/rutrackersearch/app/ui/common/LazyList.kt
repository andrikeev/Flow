package me.rutrackersearch.app.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.paging.LoadState

@Composable
fun LazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onEndOfListReached: () -> Unit = {},
    content: LazyListScope.() -> Unit
) {
    val endOfListReached by remember {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            val lastVisibleIndex = visibleItemsInfo.lastOrNull()?.index
            val lastIndex = layoutInfo.totalItemsCount - 1
            visibleItemsInfo.size > 1 && lastVisibleIndex == lastIndex
        }
    }
    LaunchedEffect(endOfListReached) {
        if (endOfListReached) {
            onEndOfListReached()
        }
    }
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        content = content
    )
}

inline fun <T> LazyListScope.dividedItems(
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    crossinline contentType: (item: T) -> Any? = { _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
) = itemsIndexed(
    items = items,
    key = key?.let { { _, item -> key(item) } },
    contentType = { _, item -> contentType(item) },
) { index, item ->
    itemContent(item)
    if (index < items.lastIndex) {
        Divider()
    }
}

fun LazyListScope.appendItems(
    state: LoadState,
    onRetryClick: () -> Unit,
) {
    when (state) {
        is LoadState.Loading -> item {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp)
                        .align(Alignment.Center),
                    strokeWidth = 2.dp,
                )
            }
        }
        is LoadState.Error -> item {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = stringResource(state.error.getStringRes()),
                    )
                    TextButton(
                        text = stringResource(R.string.action_retry),
                        onClick = onRetryClick,
                    )
                }
            }
        }
        is LoadState.NotLoading -> Unit
    }
}

fun LazyListScope.loadingItem() = item { Loading(modifier = Modifier.fillParentMaxSize()) }

fun LazyListScope.errorItem(
    error: Throwable,
    onRetryClick: () -> Unit,
) = item {
    Error(
        modifier = Modifier.fillParentMaxSize(),
        error = error, onRetryClick = onRetryClick,
    )
}

fun LazyListScope.emptyItem(
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int,
    @DrawableRes iconRes: Int,
) = item {
    Empty(
        modifier = Modifier.fillParentMaxSize(),
        titleRes = titleRes,
        subtitleRes = subtitleRes,
        iconRes = iconRes,
    )
}
