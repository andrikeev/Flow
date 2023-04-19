package flow.network.dto.topic

import kotlinx.serialization.Serializable

@Serializable
enum class TorrentStatusDto {
    Approved,
    Checking,
    Closed,
    Consumed,
    Duplicate,
    NeedEdit,
    NoDescription,
    NotApproved,
}
