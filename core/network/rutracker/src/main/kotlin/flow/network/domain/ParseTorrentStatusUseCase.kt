package flow.network.domain

import flow.network.dto.topic.TorrentStatusDto
import org.jsoup.nodes.Element

internal object ParseTorrentStatusUseCase {
    operator fun invoke(element: Element?): TorrentStatusDto? {
        return element?.let {
            when {
                element.select(".tor-dup").isNotEmpty() -> TorrentStatusDto.Duplicate
                element.select(".tor-not-approved").isNotEmpty() -> TorrentStatusDto.NotApproved
                element.select(".tor-checking").isNotEmpty() -> TorrentStatusDto.Checking
                element.select(".tor-approved").isNotEmpty() -> TorrentStatusDto.Approved
                element.select(".tor-need-edit").isNotEmpty() -> TorrentStatusDto.NeedEdit
                element.select(".tor-closed").isNotEmpty() -> TorrentStatusDto.Closed
                element.select(".tor-no-desc").isNotEmpty() -> TorrentStatusDto.NoDescription
                element.select(".tor-consumed").isNotEmpty() -> TorrentStatusDto.Consumed
                else -> null
            }
        }
    }
}
