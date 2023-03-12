package flow.domain.usecase

import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import flow.work.api.BackgroundService
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val addLocalFavoriteUseCase: AddLocalFavoriteUseCase,
    private val removeLocalFavoriteUseCase: RemoveLocalFavoriteUseCase,
    private val backgroundService: BackgroundService,
) {
    suspend operator fun <T : Topic> invoke(topic: TopicModel<T>) {
        if (topic.isFavorite) {
            removeLocalFavoriteUseCase(topic.topic.id)
            backgroundService.removeFavoriteTopic(topic.topic.id)
        } else {
            addLocalFavoriteUseCase(topic.topic)
            backgroundService.addFavoriteTopic(topic.topic.id, topic.topic is Torrent)
        }
    }
}
