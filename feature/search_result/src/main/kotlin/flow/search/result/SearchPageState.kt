package flow.search.result

import flow.domain.model.LoadStates
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Period
import flow.models.topic.TopicModel
import flow.models.topic.Torrent

internal data class SearchPageState(
    val filter: Filter,
    val appBarExpanded: Boolean = false,
    val searchContent: SearchResultContent = SearchResultContent.Initial,
    val loadStates: LoadStates = LoadStates.Idle,
)

internal sealed interface SearchResultContent {
    object Initial : SearchResultContent
    object Empty : SearchResultContent
    data class Content(
        val torrents: List<TopicModel<Torrent>>,
        val categories: List<Category>,
    ) : SearchResultContent
}

internal val SearchPageState.showFilterBadge: Boolean
    get() = !appBarExpanded &&
            (filter.period != Period.ALL_TIME ||
                    filter.author != null ||
                    !filter.categories.isNullOrEmpty())

internal val SearchPageState.categories
    get() = when (searchContent) {
        is SearchResultContent.Content -> searchContent.categories
        is SearchResultContent.Empty -> emptyList()
        is SearchResultContent.Initial -> emptyList()
    }
