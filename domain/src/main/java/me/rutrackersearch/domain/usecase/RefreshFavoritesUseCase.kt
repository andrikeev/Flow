package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.repository.FavoritesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke() {
        return favoritesRepository.loadFavorites()
    }
}
