package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.topic.CommentsPageDto
import flow.network.model.Forbidden
import flow.network.model.NotFound

internal class GetCommentsPageUseCase(
    private val api: RuTrackerInnerApi,
    private val parser: RuTrackerParser,
) {

    suspend operator fun invoke(
        token: String,
        id: String,
        page: Int?,
    ): CommentsPageDto {
        val html = api.topic(token, id, page)
        return when {
            !parser.isTopicExists(html) -> throw NotFound
            parser.isTopicModerated(html) -> throw Forbidden
            parser.isBlockedForRegion(html) -> throw Forbidden
            else -> parser.parseCommentsPage(html)
        }
    }
}
