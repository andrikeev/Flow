package flow.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import flow.designsystem.theme.AppTheme

@Composable
fun LazyList(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(AppTheme.spaces.zero),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onFirstItemVisible: () -> Unit = {},
    onLastItemVisible: () -> Unit = {},
    onLastVisibleIndexChanged: (Int) -> Unit = {},
    content: LazyListScope.() -> Unit,
) {
    val isFirstItemVisible by remember { derivedStateOf { state.isFirstItemVisible() } }
    LaunchedEffect(isFirstItemVisible) {
        if (isFirstItemVisible) {
            onFirstItemVisible()
        }
    }
    val isLastItemVisible by remember { derivedStateOf { state.isLastItemVisible() } }
    LaunchedEffect(isLastItemVisible) {
        if (isLastItemVisible) {
            onLastItemVisible()
        }
    }
    val lastVisibleItemIndex by remember { derivedStateOf { state.lastVisibleItemIndex() } }
    LaunchedEffect(lastVisibleItemIndex) {
        onLastVisibleIndexChanged(lastVisibleItemIndex)
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

private fun LazyListState.isFirstItemVisible(): Boolean {
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    val firstVisibleIndex = visibleItemsInfo.firstOrNull()?.index
    return visibleItemsInfo.size > 1 && firstVisibleIndex == 0
}

private fun LazyListState.isLastItemVisible(): Boolean {
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    val lastVisibleIndex = visibleItemsInfo.lastOrNull()?.index
    val lastIndex = layoutInfo.totalItemsCount - 1
    return visibleItemsInfo.size > 1 && lastVisibleIndex == lastIndex
}

private fun LazyListState.lastVisibleItemIndex(): Int {
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    return visibleItemsInfo.lastOrNull()?.index ?: 0
}
