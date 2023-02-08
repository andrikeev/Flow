package flow.search.result

import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Period
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import flow.ui.component.LoadStates

internal data class SearchResultState(
    val filter: Filter,
    val isAppBarExpanded: Boolean = false,
    val content: SearchResultContent = SearchResultContent.Initial,
    val loadStates: LoadStates = LoadStates.idle,
) {
    val showFilterBadge: Boolean
        get() = filter.period != Period.ALL_TIME || filter.author != null || !filter.categories.isNullOrEmpty()
}

internal sealed interface SearchResultContent {
    object Initial : SearchResultContent
    object Empty : SearchResultContent
    data class Content(
        val torrents: List<TopicModel<Torrent>>,
        val categories: List<Category>,
        val page: Int,
        val pages: Int,
    ) : SearchResultContent
}
