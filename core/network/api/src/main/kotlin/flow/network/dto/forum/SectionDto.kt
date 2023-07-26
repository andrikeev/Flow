package flow.network.dto.forum

import kotlinx.serialization.Serializable

@Serializable
data class SectionDto(
    val name: String,
    val topics: List<String>,
)
