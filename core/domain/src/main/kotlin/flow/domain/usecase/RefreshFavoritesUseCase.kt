package flow.domain.usecase

import javax.inject.Inject

class RefreshFavoritesUseCase @Inject constructor(
    private val loadFavoritesUseCase: LoadFavoritesUseCase,
    private val syncFavoritesUseCase: SyncFavoritesUseCase,
) {
    suspend operator fun invoke() {
        loadFavoritesUseCase()
        syncFavoritesUseCase()
    }
}
