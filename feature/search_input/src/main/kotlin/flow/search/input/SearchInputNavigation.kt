package flow.search.input

import androidx.lifecycle.SavedStateHandle
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.models.topic.Author
import flow.navigation.NavigationController
import flow.navigation.model.NavigationArgument
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.model.appendOptionalArgs
import flow.navigation.model.appendOptionalParams
import flow.navigation.model.buildRoute
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel

private const val QueryKey = "query"
private const val CategoriesKey = "categories"
private const val AuthorNameKey = "author_name"
private const val AuthorIdKey = "author_id"
private const val SortKey = "sort"
private const val OrderKey = "order"
private const val PeriodKey = "period"
private const val SearchInputRoute = "search_input"

context(NavigationGraphBuilder)
fun addSearchInput(
    back: () -> Unit,
    openSearchResult: (Filter) -> Unit,
    animations: NavigationAnimations,
) {
    addDestination(
        route = buildRoute(
            route = SearchInputRoute,
            optionalArgsBuilder = {
                appendOptionalArgs(
                    QueryKey,
                    CategoriesKey,
                    AuthorNameKey,
                    AuthorIdKey,
                    SortKey,
                    OrderKey,
                    PeriodKey,
                )
            },
        ),
        arguments = listOf(
            NavigationArgument(QueryKey, true),
            NavigationArgument(CategoriesKey, true),
            NavigationArgument(AuthorNameKey, true),
            NavigationArgument(AuthorIdKey, true),
            NavigationArgument(SortKey, true),
            NavigationArgument(OrderKey, true),
            NavigationArgument(PeriodKey, true),
        ),
        animations = animations,
    ) {
        SearchInputScreen(
            viewModel = viewModel(),
            back = back,
            openSearchResult = openSearchResult,
        )
    }
}

context(NavigationGraphBuilder, NavigationController)
fun openSearchInput(filter: Filter = Filter()) {
    navigate(
        buildRoute(
            route = SearchInputRoute,
            optionalArgsBuilder = {
                appendOptionalParams(
                    QueryKey to filter.query?.takeIf(String::isNotBlank),
                    CategoriesKey to filter.categories.queryParam(),
                    AuthorIdKey to filter.author?.id,
                    AuthorIdKey to filter.author?.name,
                    SortKey to filter.sort.queryParam,
                    OrderKey to filter.order.queryParam,
                    PeriodKey to filter.period.queryParam,
                )
            },
        ),
    )
}

context(NavigationGraphBuilder, NavigationController)
fun openSearchInput(categoryId: String) {
    navigate(
        buildRoute(
            route = SearchInputRoute,
            optionalArgsBuilder = {
                appendOptionalParams(
                    CategoriesKey to categoryId,
                )
            },
        ),
    )
}

internal val SavedStateHandle.filter: Filter
    get() = Filter(
        query = get(QueryKey),
        categories = categories,
        author = author,
        sort = Sort.fromQueryParam(get(SortKey)),
        order = Order.fromQueryParam(get(OrderKey)),
        period = Period.fromQueryParam(get(PeriodKey)),
    )

private val SavedStateHandle.categories: List<Category>?
    get() = get<String>(CategoriesKey)
        ?.split(",")
        ?.map { Category(it, "") }

private val SavedStateHandle.author: Author?
    get() = if (contains(AuthorIdKey) || contains(AuthorNameKey)) {
        Author(get(AuthorIdKey), get<String>(AuthorNameKey).orEmpty())
    } else {
        null
    }

private fun List<Category>?.queryParam(): String? {
    return this?.takeIf(List<Category>::isNotEmpty)
        ?.map(Category::id)
        ?.joinToString(",")
}

private val Sort.queryParam
    get() = when (this) {
        Sort.DATE -> "1"
        Sort.TITLE -> "2"
        Sort.DOWNLOADED -> "4"
        Sort.SEEDS -> "10"
        Sort.LEECHES -> "11"
        Sort.SIZE -> "7"
    }

private fun Sort.Companion.fromQueryParam(param: String?) = when (param) {
    "1" -> Sort.DATE
    "2" -> Sort.TITLE
    "4" -> Sort.DOWNLOADED
    "10" -> Sort.SEEDS
    "11" -> Sort.LEECHES
    "7" -> Sort.SIZE
    else -> Sort.DATE
}

private val Order.queryParam
    get() = when (this) {
        Order.ASCENDING -> "1"
        Order.DESCENDING -> "2"
    }

private fun Order.Companion.fromQueryParam(param: String?) = when (param) {
    "1" -> Order.ASCENDING
    "2" -> Order.DESCENDING
    else -> Order.ASCENDING
}

private val Period.queryParam
    get() = when (this) {
        Period.ALL_TIME -> "-1"
        Period.TODAY -> "1"
        Period.LAST_THREE_DAYS -> "3"
        Period.LAST_WEEK -> "7"
        Period.LAST_TWO_WEEKS -> "14"
        Period.LAST_MONTH -> "32"
    }

private fun Period.Companion.fromQueryParam(param: String?) = when (param) {
    "-1" -> Period.ALL_TIME
    "1" -> Period.TODAY
    "3" -> Period.LAST_THREE_DAYS
    "7" -> Period.LAST_WEEK
    "14" -> Period.LAST_TWO_WEEKS
    "32" -> Period.LAST_MONTH
    else -> Period.ALL_TIME
}
