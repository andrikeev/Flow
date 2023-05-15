package flow.domain.usecase

import flow.data.api.repository.FavoritesRepository
import flow.dispatchers.api.Dispatchers
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val refreshFavoritesUseCase: RefreshFavoritesUseCase,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(scope: CoroutineScope): Flow<List<TopicModel<out Topic>>> {
        scope.launch(dispatchers.default) { refreshFavoritesUseCase() }
        return favoritesRepository.observeTopics()
            .distinctUntilChanged()
            .catch {
                favoritesRepository.clear()
                emit(emptyList())
            }
    }
}
