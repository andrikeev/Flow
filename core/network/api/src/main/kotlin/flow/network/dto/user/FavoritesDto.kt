package flow.network.dto.user

import flow.network.dto.topic.ForumTopicDto
import kotlinx.serialization.Serializable

@Serializable
data class FavoritesDto(val topics: List<ForumTopicDto>)
