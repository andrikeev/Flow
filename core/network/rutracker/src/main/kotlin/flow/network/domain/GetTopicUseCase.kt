package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.topic.ForumTopicDto
import flow.network.model.Forbidden
import flow.network.model.NotFound

internal class GetTopicUseCase(
    private val api: RuTrackerInnerApi,
    private val parser: RuTrackerParser,
) {

    suspend operator fun invoke(
        token: String,
        id: String,
        page: Int?,
    ): ForumTopicDto {
        val html = api.topic(token, id, page)
        return when {
            !parser.isTopicExists(html) -> throw NotFound
            parser.isTopicModerated(html) -> throw Forbidden
            parser.isBlockedForRegion(html) -> throw Forbidden
            else -> parseTopic(html)
        }
    }

    private fun parseTopic(html: String) = if (parser.isTorrentTopic(html)) {
        parser.parseTorrent(html)
    } else {
        parser.parseCommentsPage(html)
    }
}
