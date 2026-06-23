package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.topic.TopicPageDto
import flow.network.model.Forbidden
import flow.network.model.NotFound

internal class GetTopicPageUseCase(
    private val api: RuTrackerInnerApi,
    private val parser: RuTrackerParser,
) {
    suspend operator fun invoke(
        token: String,
        id: String,
        page: Int?,
    ): TopicPageDto {
        val html = api.topic(token, id, page)
        return when {
            !parser.isTopicExists(html) -> throw NotFound
            parser.isTopicModerated(html) -> throw Forbidden
            parser.isBlockedForRegion(html) -> throw Forbidden
            else -> parser.parseTopicPage(html)
        }
    }
}
