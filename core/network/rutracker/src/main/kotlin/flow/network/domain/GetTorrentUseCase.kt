package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.topic.TorrentDto
import flow.network.model.Forbidden
import flow.network.model.NotFound

internal class GetTorrentUseCase(
    private val api: RuTrackerInnerApi,
    private val parser: RuTrackerParser,
) {
    suspend operator fun invoke(token: String, id: String): TorrentDto {
        val html = api.topic(token, id)
        return when {
            !parser.isTopicExists(html) -> throw NotFound
            parser.isTopicModerated(html) -> throw Forbidden
            parser.isBlockedForRegion(html) -> throw Forbidden
            else -> parser.parseTorrent(html)
        }
    }
}
