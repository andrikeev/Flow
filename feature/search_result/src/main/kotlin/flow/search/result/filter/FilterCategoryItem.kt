package flow.search.result.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.VisibilityState
import flow.designsystem.component.Icon
import flow.designsystem.component.LazyList
import flow.designsystem.component.Page
import flow.designsystem.component.PagesScreen
import flow.designsystem.component.Surface
import flow.designsystem.component.TextButton
import flow.designsystem.component.rememberVisibilityState
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.models.forum.Category
import flow.search.result.R
import flow.search.result.categories.CategorySelectionScreen
import flow.ui.component.SelectableCategoryListItem
import flow.ui.component.dividedItems

@Composable
internal fun FilterCategoryItem(
    available: List<Category>,
    selected: List<Category>?,
    onSelect: (List<Category>?) -> Unit,
) {
    val dialogState = rememberVisibilityState()
    CategoriesSelectDialog(
        state = dialogState,
        available = available,
        selected = selected,
        onSubmit = { categories ->
            onSelect(categories)
            dialogState.hide()
        },
        onDismiss = dialogState::hide,
    )
    FilterBarItem(
        label = stringResource(R.string.search_screen_filter_category_label),
        onClick = dialogState::show,
    ) {
        BodyLarge(
            modifier = Modifier.weight(1f),
            text = when {
                selected.isNullOrEmpty() -> stringResource(R.string.search_screen_filter_any)
                selected.size == 1 -> selected.first().name
                else -> stringResource(
                    R.string.search_screen_filter_category_counter,
                    selected.size
                )
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(icon = FlowIcons.Forum, contentDescription = null)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoriesSelectDialog(
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
                                AvailableCategoriesList(
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
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun AvailableCategoriesList(
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

