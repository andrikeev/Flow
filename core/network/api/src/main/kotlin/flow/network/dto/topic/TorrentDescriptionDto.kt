package flow.network.dto.topic

import kotlinx.serialization.Serializable

@Serializable
data class TorrentDescriptionDto(val children: List<PostElementDto>)
