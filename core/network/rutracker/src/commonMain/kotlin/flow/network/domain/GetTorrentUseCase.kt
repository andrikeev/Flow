package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.topic.TorrentDto
import flow.network.model.Forbidden
import flow.network.model.NotFound

internal class GetTorrentUseCase(
    private val api: RuTrackerInnerApi,
    private val parseTorrentUseCase: ParseTorrentUseCase,
) {
    suspend operator fun invoke(token: String, id: String): TorrentDto {
        val html = api.topic(token, id)
        return when {
            !isTopicExists(html) -> throw NotFound
            isTopicModerated(html) -> throw Forbidden
            isBlockedForRegion(html) -> throw Forbidden
            else -> parseTorrentUseCase(html)
        }
    }
}
