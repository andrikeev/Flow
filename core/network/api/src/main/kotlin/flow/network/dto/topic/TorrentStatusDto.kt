package flow.network.dto.topic

import kotlinx.serialization.Serializable

@Serializable
enum class TorrentStatusDto {
    DUPLICATE,
    NOT_APPROVED,
    CHECKING,
    APPROVED,
    NEED_EDIT,
    CLOSED,
    NO_DESCRIPTION,
    CONSUMED,
}