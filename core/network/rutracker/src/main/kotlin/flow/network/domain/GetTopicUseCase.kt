package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.ResultDto
import flow.network.dto.error.FlowError
import flow.network.dto.topic.ForumTopicDto

internal class GetTopicUseCase(
    private val api: RuTrackerInnerApi,
    private val parseTorrentUseCase: ParseTorrentUseCase,
    private val parseCommentsPageUseCase: ParseCommentsPageUseCase,
) {

    suspend operator fun invoke(
        token: String,
        id: String?,
        pid: String?,
        page: Int?,
    ): ResultDto<ForumTopicDto> = tryCatching {
        val html = api.topic(token, id, pid, page)
        return when {
            !isTopicExists(html) -> ResultDto.Error(FlowError.NotFound)
            isTopicModerated(html) -> ResultDto.Error(FlowError.NotFound)
            isBlockedForRegion(html) -> ResultDto.Error(FlowError.NotFound)
            else -> parseTopic(html).toResult()
        }
    }

    private fun parseTopic(html: String) = if (html.contains("magnet-link")) {
        parseTorrentUseCase(html)
    } else {
        parseCommentsPageUseCase(html)
    }
}
