package flow.domain.usecase

import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext

class RefreshFavoritesUseCase(
    private val loadFavoritesUseCase: LoadFavoritesUseCase,
    private val syncFavoritesUseCase: SyncFavoritesUseCase,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke() {
        withContext(dispatchers.default) {
            loadFavoritesUseCase()
            syncFavoritesUseCase()
        }
    }
}
