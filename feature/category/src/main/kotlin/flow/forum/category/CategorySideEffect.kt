package flow.forum.category

import flow.models.topic.Topic
import flow.models.topic.Torrent

internal sealed interface CategorySideEffect {
    object Back : CategorySideEffect
    data class OpenCategory(val categoryId: String) : CategorySideEffect
    data class OpenSearch(val categoryId: String) : CategorySideEffect
    data class OpenTopic(val topic: Topic) : CategorySideEffect
    data class OpenTorrent(val torrent: Torrent) : CategorySideEffect
    object ShowLoginDialog : CategorySideEffect
    object OpenLogin : CategorySideEffect
}

