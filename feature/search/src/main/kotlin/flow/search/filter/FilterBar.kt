package flow.search.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.models.topic.Author
import flow.search.R
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
) {
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
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
}
