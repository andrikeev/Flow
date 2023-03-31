package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.topic.ForumTopicDto
import flow.network.model.Forbidden
import flow.network.model.NotFound

internal class GetTopicUseCase(
    private val api: RuTrackerInnerApi,
    private val parseTorrentUseCase: ParseTorrentUseCase,
    private val parseCommentsPageUseCase: ParseCommentsPageUseCase,
) {

    suspend operator fun invoke(
        token: String,
        id: String,
        page: Int?,
    ): ForumTopicDto {
        val html = api.topic(token, id, page)
        return when {
            !isTopicExists(html) -> throw NotFound
            isTopicModerated(html) -> throw Forbidden
            isBlockedForRegion(html) -> throw Forbidden
            else -> parseTopic(html)
        }
    }

    private fun parseTopic(html: String) = if (html.contains("magnet-link")) {
        parseTorrentUseCase(html)
    } else {
        parseCommentsPageUseCase(html)
    }
}
