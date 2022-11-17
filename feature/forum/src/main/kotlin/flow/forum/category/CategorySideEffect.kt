package flow.forum.category

import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Topic
import flow.models.topic.Torrent

internal sealed interface CategorySideEffect {
    object Back : CategorySideEffect
    data class OpenCategory(val category: Category) : CategorySideEffect
    data class OpenSearch(val filter: Filter) : CategorySideEffect
    data class OpenTopic(val topic: Topic) : CategorySideEffect
    data class OpenTorrent(val torrent: Torrent) : CategorySideEffect
}

