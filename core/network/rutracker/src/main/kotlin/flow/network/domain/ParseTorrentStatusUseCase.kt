package flow.network.domain

import flow.network.dto.topic.TorrentStatusDto
import org.jsoup.nodes.Element

internal object ParseTorrentStatusUseCase {
    operator fun invoke(element: Element?): TorrentStatusDto? {
        return element?.let {
            when {
                element.select(".tor-dup").isNotEmpty() -> TorrentStatusDto.DUPLICATE
                element.select(".tor-not-approved").isNotEmpty() -> TorrentStatusDto.NOT_APPROVED
                element.select(".tor-checking").isNotEmpty() -> TorrentStatusDto.CHECKING
                element.select(".tor-approved").isNotEmpty() -> TorrentStatusDto.APPROVED
                element.select(".tor-need-edit").isNotEmpty() -> TorrentStatusDto.NEED_EDIT
                element.select(".tor-closed").isNotEmpty() -> TorrentStatusDto.CLOSED
                element.select(".tor-no-desc").isNotEmpty() -> TorrentStatusDto.NO_DESCRIPTION
                element.select(".tor-consumed").isNotEmpty() -> TorrentStatusDto.CONSUMED
                else -> null
            }
        }
    }
}
