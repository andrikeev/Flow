package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val refreshFavoritesUseCase: RefreshFavoritesUseCase,
) {
    operator fun invoke(): Flow<List<TopicModel<out Topic>>> {
        return favoritesRepository.observeTopics()
            .onStart { refreshFavoritesUseCase() }
            .catch { favoritesRepository.clear() }
    }
}
