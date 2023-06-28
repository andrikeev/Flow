package flow.search.result.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import flow.designsystem.component.InvertedSurface
import flow.designsystem.component.Text
import flow.designsystem.theme.AppTheme
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.models.topic.Author
import flow.search.result.R
import flow.ui.component.resId

@Composable
internal fun FilterBar(
    filter: Filter,
    categories: List<Category>,
    onSelectSort: (Sort) -> Unit,
    onSelectOrder: (Order) -> Unit,
    onSelectPeriod: (Period) -> Unit,
    onSelectAuthor: (Author?) -> Unit,
    onSelectCategories: (List<Category>?) -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(
            top = AppTheme.spaces.medium,
            bottom = AppTheme.spaces.large,
        ),
) {
    FilterDropdownItem(
        label = stringResource(R.string.search_screen_filter_sort_label),
        items = Sort.values().toList(),
        selected = filter.sort,
        itemLabel = { sort -> stringResource(sort.resId) },
        onSelect = onSelectSort,
    )
    FilterDropdownItem(
        label = stringResource(R.string.search_screen_filter_order_label),
        items = Order.values().toList(),
        selected = filter.order,
        itemLabel = { order -> stringResource(order.resId) },
        onSelect = onSelectOrder,
    )
    FilterDropdownItem(
        label = stringResource(R.string.search_screen_filter_period_label),
        items = Period.values().toList(),
        selected = filter.period,
        itemLabel = { period -> stringResource(period.resId).replaceFirstChar(Char::uppercaseChar) },
        onSelect = onSelectPeriod,
    )
    FilterAuthorItem(
        selected = filter.author,
        onSubmit = onSelectAuthor,
    )
    FilterCategoryItem(
        available = categories,
        selected = filter.categories,
        onSelect = onSelectCategories,
    )
}

@Composable
internal fun FilterBarItem(
    label: String,
    content: @Composable RowScope.() -> Unit,
) = Row(
    modifier = Modifier.padding(
        horizontal = AppTheme.spaces.large,
        vertical = AppTheme.spaces.small,
    ),
    verticalAlignment = Alignment.CenterVertically,
) {
    Text(modifier = Modifier.weight(1f), text = label)
    content()
}

@Composable
internal fun RowScope.FilterBarItemContent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) = InvertedSurface(
    modifier = modifier
        .weight(2f)
        .height(AppTheme.sizes.default),
    shape = AppTheme.shapes.small,
    tonalElevation = AppTheme.elevations.medium,
    onClick = onClick,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
