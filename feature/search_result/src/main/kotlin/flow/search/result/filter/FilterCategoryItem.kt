package flow.search.result.filter

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Icon
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.models.forum.Category
import flow.search.result.R
import flow.search.result.categories.CategorySelectionDialog
import flow.ui.component.rememberVisibilityState

@Composable
internal fun FilterCategoryItem(
    available: List<Category>,
    selected: List<Category>?,
    onSelect: (List<Category>?) -> Unit,
) {
    val dialogState = rememberVisibilityState()
    CategorySelectionDialog(
        state = dialogState,
        available = available,
        selected = selected,
        onSubmit = { categories ->
            onSelect(categories)
            dialogState.hide()
        },
        onDismiss = dialogState::hide,
    )
    FilterBarItem(label = stringResource(R.string.search_screen_filter_category_label)) {
        FilterBarItemContent(onClick = dialogState::show) {
            BodyLarge(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = AppTheme.spaces.large),
                text = when {
                    selected.isNullOrEmpty() -> stringResource(R.string.search_screen_filter_any)
                    selected.size == 1 -> selected.first().name
                    else -> stringResource(
                        R.string.search_screen_filter_category_counter,
                        selected.size,
                    )
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                modifier = Modifier.padding(AppTheme.spaces.medium),
                icon = FlowIcons.Forum,
                contentDescription = null,
            )
        }
    }
}
