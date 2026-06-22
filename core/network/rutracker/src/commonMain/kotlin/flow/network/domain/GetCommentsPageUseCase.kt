package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.topic.CommentsPageDto
import flow.network.model.Forbidden
import flow.network.model.NotFound

internal class GetCommentsPageUseCase(
    private val api: RuTrackerInnerApi,
    private val parseCommentsPageUseCase: ParseCommentsPageUseCase,
) {

    suspend operator fun invoke(
        token: String,
        id: String,
        page: Int?,
    ): CommentsPageDto {
        val html = api.topic(token, id, page)
        return when {
            !isTopicExists(html) -> throw NotFound
            isTopicModerated(html) -> throw Forbidden
            isBlockedForRegion(html) -> throw Forbidden
            else -> parseCommentsPageUseCase(html)
        }
    }
}
