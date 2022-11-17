package flow.search.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import flow.designsystem.component.LazyList
import flow.designsystem.component.Page
import flow.designsystem.component.PagesScreen
import flow.designsystem.component.TextButton
import flow.designsystem.theme.Border
import flow.designsystem.theme.Elevation
import flow.models.forum.Category
import flow.search.R
import flow.search.categories.CategorySelectionScreen
import flow.ui.component.SelectableCategoryListItem
import flow.ui.component.dividedItems

@Composable
internal fun FilterCategoryItem(
    available: List<Category>,
    selected: List<Category>?,
    onSelect: (List<Category>?) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var showDialog by remember { mutableStateOf(false) }
        if (showDialog) {
            CategoriesSelectDialog(
                available = available,
                selected = selected,
                onSubmit = { categories ->
                    onSelect(categories)
                    showDialog = false
                },
                onDismiss = { showDialog = false },
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.search_screen_filter_category_label),
        )
        Surface(
            modifier = Modifier.weight(2f),
            shape = MaterialTheme.shapes.small,
            border = Border.outline,
            onClick = { showDialog = true },
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = when {
                    selected == null -> stringResource(R.string.search_screen_filter_any)
                    selected.size == 1 -> selected.first().name
                    else -> stringResource(R.string.search_screen_filter_category_counter, selected.size)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
    Dialog(onDismissRequest = onDismiss) {
        Surface {
            Column {
                val selectedState = remember { mutableStateListOf(*selected.orEmpty().toTypedArray()) }
                PagesScreen(
                    modifier = Modifier.weight(1f), pages = listOf(
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
                    )
                )
                Surface(tonalElevation = Elevation.small) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
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
        }
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
        contentPadding = PaddingValues(vertical = 8.dp),
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
