package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.ResultDto
import flow.network.dto.error.FlowError
import flow.network.dto.topic.TorrentDto

internal class GetTorrentUseCase(
    private val api: RuTrackerInnerApi,
    private val parseTorrentUseCase: ParseTorrentUseCase,
) {
    suspend operator fun invoke(token: String, id: String?): ResultDto<TorrentDto> = tryCatching {
        val html = api.topic(token, id)
        return when {
            !isTopicExists(html) -> ResultDto.Error(FlowError.NotFound)
            isTopicModerated(html) -> ResultDto.Error(FlowError.NotFound)
            isBlockedForRegion(html) -> ResultDto.Error(FlowError.NotFound)
            else -> parseTorrentUseCase(html).toResult()
        }
    }
}
