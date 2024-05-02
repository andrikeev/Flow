package flow.search.result.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.window.Dialog
import flow.designsystem.component.Divider
import flow.designsystem.component.LazyList
import flow.designsystem.component.Page
import flow.designsystem.component.PagesScreen
import flow.designsystem.component.Surface
import flow.designsystem.component.TextButton
import flow.designsystem.theme.AppTheme
import flow.models.forum.Category
import flow.search.result.R
import flow.ui.component.VisibilityState

@Composable
internal fun CategorySelectionDialog(
    state: VisibilityState,
    available: List<Category>,
    selected: List<Category>?,
    onSubmit: (List<Category>?) -> Unit,
    onDismiss: () -> Unit,
) {
    if (state.visible) {
        val selectedState = remember { mutableStateListOf(*selected.orEmpty().toTypedArray()) }
        Dialog(
            onDismissRequest = onDismiss,
            content = {
                Column(modifier = Modifier.clip(AppTheme.shapes.large)) {
                    PagesScreen(
                        pages = listOf(
                            Page(labelResId = R.string.search_screen_filter_categories_current) {
                                CategorySelectionList(
                                    allCategories = available,
                                    selectedCategories = selectedState,
                                    onClick = { category ->
                                        if (selectedState.contains(category)) {
                                            selectedState.remove(category)
                                        } else {
                                            selectedState.add(category)
                                        }
                                    },
                                )
                            },
                            Page(labelResId = R.string.search_screen_filter_categories_all) {
                                CategorySelectionScreen(
                                    selectedCategories = selectedState,
                                    onCategoriesSelected = selectedState::addAll,
                                    onCategoriesRemoved = selectedState::removeAll,
                                )
                            },
                        ),
                        bottomBar = {
                            Surface(tonalElevation = AppTheme.elevations.medium) {
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = AppTheme.spaces.large,
                                            vertical = AppTheme.spaces.small,
                                        ),
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    TextButton(
                                        text = stringResource(flow.designsystem.R.string.designsystem_action_cancel),
                                        onClick = onDismiss,
                                    )
                                    if (!selected.isNullOrEmpty()) {
                                        TextButton(
                                            text = stringResource(flow.designsystem.R.string.designsystem_action_reset),
                                            onClick = { onSubmit(null) },
                                        )
                                    }
                                    TextButton(
                                        text = stringResource(flow.designsystem.R.string.designsystem_action_apply),
                                        onClick = { onSubmit(selectedState.takeIf(List<Category>::isNotEmpty)) },
                                    )
                                }
                            }
                        },
                    )
                }
            },
        )
    }
}

@Composable
private fun CategorySelectionList(
    allCategories: List<Category>,
    selectedCategories: List<Category>,
    onClick: (Category) -> Unit,
) {
    val categories = remember { LinkedHashSet(allCategories).plus(selectedCategories).toList() }
    LazyList(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(vertical = AppTheme.spaces.medium),
        verticalArrangement = Arrangement.Top,
    ) {
        dividedItems(
            items = categories,
            key = Category::id,
        ) { item ->
            val selected = selectedCategories.contains(item)
            SelectableCategoryListItem(
                text = item.name,
                selected = if (selected) ToggleableState.On else ToggleableState.Off,
                onSelect = { onClick(item) },
            )
        }
    }
}

internal inline fun <T> LazyListScope.dividedItems(
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
