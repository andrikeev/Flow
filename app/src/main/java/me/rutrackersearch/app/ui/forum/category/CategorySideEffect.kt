package me.rutrackersearch.app.ui.forum.category

import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent

sealed interface CategorySideEffect {
    object Back : CategorySideEffect
    data class OpenCategory(val category: Category) : CategorySideEffect
    data class OpenSearch(val filter: Filter) : CategorySideEffect
    data class OpenTopic(val topic: Topic) : CategorySideEffect
    data class OpenTorrent(val torrent: Torrent) : CategorySideEffect
}

