package flow.topic

import flow.models.search.Filter

internal sealed interface TopicSideEffect {
    data class OpenCategory(val id: String) : TopicSideEffect
    data class OpenFile(val uri: String) : TopicSideEffect
    data class OpenSearch(val filter: Filter) : TopicSideEffect
    data class ShareLink(val link: String) : TopicSideEffect
    data class ShowMagnet(val link: String) : TopicSideEffect
    data object Back : TopicSideEffect
    data object OpenLogin : TopicSideEffect
    data object ShowAddCommentDialog : TopicSideEffect
    data object ShowAddCommentError : TopicSideEffect
    data object ShowDownloadProgress : TopicSideEffect
    data object ShowFavoriteToggleError : TopicSideEffect
    data object ShowLoginRequired : TopicSideEffect
}
