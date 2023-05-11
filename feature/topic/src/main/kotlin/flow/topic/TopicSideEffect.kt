package flow.topic

import flow.models.search.Filter

internal sealed interface TopicSideEffect {
    object Back: TopicSideEffect
    data class OpenCategory(val id: String) : TopicSideEffect
    data class OpenFile(val uri: String) : TopicSideEffect
    object OpenLogin: TopicSideEffect
    data class OpenSearch(val filter: Filter) : TopicSideEffect
    data class ShareLink(val link: String) : TopicSideEffect
    object ShowAddCommentDialog: TopicSideEffect
    object ShowAddCommentError: TopicSideEffect
    object ShowDownloadProgress : TopicSideEffect
    object ShowLoginRequired: TopicSideEffect
    data class ShowMagnet(val link: String) : TopicSideEffect
}
