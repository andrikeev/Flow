package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.models.topic.Topic
import javax.inject.Inject

class AddLocalFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(topic: Topic) {
        favoritesRepository.add(topic)
    }
}
