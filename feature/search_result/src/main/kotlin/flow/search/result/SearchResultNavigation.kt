package flow.search.result

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.models.topic.Author
import flow.navigation.viewModel
import kotlinx.serialization.Serializable

@Serializable
data class SearchResultRoute(
    val query: String? = null,
    val categoryIds: String? = null,
    val authorId: String? = null,
    val authorName: String? = null,
    val sort: String? = null,
    val order: String? = null,
    val period: String? = null,
) : NavKey {
    constructor(filter: Filter) : this(
        query = filter.query?.takeIf(String::isNotBlank),
        categoryIds = filter.categories?.takeIf(List<Category>::isNotEmpty)
            ?.joinToString(separator = ",", transform = Category::id),
        authorId = filter.author?.id,
        authorName = filter.author?.name,
        sort = filter.sort.toQueryParam(),
        order = filter.order.toQueryParam(),
        period = filter.period.toQueryParam(),
    )

    internal fun toFilter(): Filter = Filter(
        query = query,
        categories = categoryIds?.split(",")?.map { Category(it, "") },
        author = if (authorId != null || authorName != null) {
            Author(id = authorId, name = authorName.orEmpty())
        } else {
            null
        },
        sort = Sort.fromQueryParam(sort),
        order = Order.fromQueryParam(order),
        period = Period.fromQueryParam(period),
    )
}

fun EntryProviderScope<NavKey>.addSearchResult(
    back: () -> Unit,
    openSearchInput: (filter: Filter) -> Unit,
    openSearchResult: (filter: Filter) -> Unit,
    openTopic: (id: String) -> Unit,
) {
    entry<SearchResultRoute> { key ->
        SearchResultScreen(
            viewModel = viewModel<SearchResultViewModel, SearchResultViewModel.Factory> { factory ->
                factory.create(key.toFilter())
            },
            back = back,
            openSearchInput = openSearchInput,
            openSearchResult = openSearchResult,
            openTopic = openTopic,
        )
    }
}

private fun Sort.toQueryParam(): String = when (this) {
    Sort.DATE -> "1"
    Sort.TITLE -> "2"
    Sort.DOWNLOADED -> "4"
    Sort.SEEDS -> "10"
    Sort.LEECHES -> "11"
    Sort.SIZE -> "7"
}

private fun Sort.Companion.fromQueryParam(param: String?): Sort = when (param) {
    "1" -> Sort.DATE
    "2" -> Sort.TITLE
    "4" -> Sort.DOWNLOADED
    "10" -> Sort.SEEDS
    "11" -> Sort.LEECHES
    "7" -> Sort.SIZE
    else -> Sort.DATE
}

private fun Order.toQueryParam(): String = when (this) {
    Order.ASCENDING -> "1"
    Order.DESCENDING -> "2"
}

private fun Order.Companion.fromQueryParam(param: String?): Order = when (param) {
    "1" -> Order.ASCENDING
    "2" -> Order.DESCENDING
    else -> Order.ASCENDING
}

private fun Period.toQueryParam(): String = when (this) {
    Period.ALL_TIME -> "-1"
    Period.TODAY -> "1"
    Period.LAST_THREE_DAYS -> "3"
    Period.LAST_WEEK -> "7"
    Period.LAST_TWO_WEEKS -> "14"
    Period.LAST_MONTH -> "32"
}

private fun Period.Companion.fromQueryParam(param: String?): Period = when (param) {
    "-1" -> Period.ALL_TIME
    "1" -> Period.TODAY
    "3" -> Period.LAST_THREE_DAYS
    "7" -> Period.LAST_WEEK
    "14" -> Period.LAST_TWO_WEEKS
    "32" -> Period.LAST_MONTH
    else -> Period.ALL_TIME
}
