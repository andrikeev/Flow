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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import flow.designsystem.component.CircularProgressIndicator
import flow.designsystem.component.Divider
import flow.designsystem.component.Empty
import flow.designsystem.component.Error
import flow.designsystem.component.Loading
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.theme.AppTheme
import flow.models.LoadState
import flow.ui.R
import flow.designsystem.R as dsR

inline fun <T> LazyListScope.dividedItems(
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    crossinline contentType: (item: T) -> Any? = { _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit,
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
                    .height(AppTheme.sizes.default)
                    .fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(AppTheme.spaces.large)
                        .size(AppTheme.sizes.medium)
                        .align(Alignment.Center),
                )
            }
        }

        is LoadState.Error -> item {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppTheme.spaces.large,
                            vertical = AppTheme.spaces.mediumLarge,
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = stringResource(state.error.getStringRes()),
                    )
                    TextButton(
                        text = stringResource(dsR.string.designsystem_action_retry),
                        onClick = onRetryClick,
                    )
                }
            }
        }

        is LoadState.NotLoading -> Unit
    }
}

fun LazyListScope.loadingItem(
    fillParentMaxSize: Boolean = true,
) = item {
    Loading(
        modifier = if (fillParentMaxSize) Modifier.fillParentMaxSize() else Modifier,
    )
}

fun LazyListScope.errorItem(
    error: Throwable,
    fillParentMaxSize: Boolean = true,
    onRetryClick: (() -> Unit)? = null,
) = item {
    Error(
        modifier = if (fillParentMaxSize) Modifier.fillParentMaxSize() else Modifier,
        titleRes = error.getErrorTitleRes(),
        subtitleRes = error.getStringRes(),
        imageRes = error.getIllRes(),
        onRetryClick = onRetryClick,
    )
}

fun LazyListScope.errorItem(
    @StringRes titleRes: Int = R.string.error_title,
    @StringRes subtitleRes: Int = R.string.error_something_goes_wrong,
    @DrawableRes imageRes: Int = R.drawable.ill_error,
    fillParentMaxSize: Boolean = true,
    onRetryClick: (() -> Unit)? = null,
) = item {
    Error(
        modifier = if (fillParentMaxSize) Modifier.fillParentMaxSize() else Modifier,
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
    fillParentMaxSize: Boolean = true,
) = item {
    Empty(
        modifier = if (fillParentMaxSize) Modifier.fillParentMaxSize() else Modifier,
        titleRes = titleRes,
        subtitleRes = subtitleRes,
        imageRes = imageRes,
    )
}
