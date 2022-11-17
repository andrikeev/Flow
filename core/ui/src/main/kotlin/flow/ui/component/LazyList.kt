package flow.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import flow.designsystem.component.Divider
import flow.designsystem.component.Empty
import flow.designsystem.component.Error
import flow.designsystem.component.Loading
import flow.designsystem.component.TextButton
import flow.ui.R
import flow.designsystem.R as DesignsystemR

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
            Box(
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
                        text = stringResource(DesignsystemR.string.designsystem_action_retry),
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
        titleRes = error.getStringRes(),
        subtitleRes = error.getStringRes(),
        imageRes = error.getIllRes(),
        onRetryClick = onRetryClick,
    )
}

fun LazyListScope.errorItem(
    @StringRes titleRes: Int = R.string.error_title,
    @StringRes subtitleRes: Int = R.string.error_something_goes_wrong,
    @DrawableRes imageRes: Int = R.drawable.ill_error,
    onRetryClick: () -> Unit,
) = item {
    Error(
        modifier = Modifier.fillParentMaxSize(),
        titleRes = titleRes,
        subtitleRes = subtitleRes,
        imageRes = imageRes,
        onRetryClick = onRetryClick,
    )
}

fun LazyListScope.emptyItem(
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int,
    @DrawableRes imageRes: Int,
) = item {
    Empty(
        modifier = Modifier.fillParentMaxSize(),
        titleRes = titleRes,
        subtitleRes = subtitleRes,
        imageRes = imageRes,
    )
}
