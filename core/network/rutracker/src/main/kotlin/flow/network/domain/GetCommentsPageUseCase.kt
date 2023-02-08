package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.ResultDto
import flow.network.dto.error.FlowError
import flow.network.dto.topic.CommentsPageDto

internal class GetCommentsPageUseCase(
    private val api: RuTrackerInnerApi,
    private val parseCommentsPageUseCase: ParseCommentsPageUseCase,
) {

    suspend operator fun invoke(
        token: String,
        id: String?,
        pid: String?,
        page: Int?,
    ): ResultDto<CommentsPageDto> = tryCatching {
        val html = api.topic(token = token, id = id, pid = pid, page = page)
        return when {
            !isTopicExists(html) -> ResultDto.Error(FlowError.NotFound)
            isTopicModerated(html) -> ResultDto.Error(FlowError.NotFound)
            isBlockedForRegion(html) -> ResultDto.Error(FlowError.NotFound)
            else -> parseCommentsPageUseCase(html).toResult()
        }
    }
}
