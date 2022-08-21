package me.rutrackersearch.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.models.forum.CategoryModel
import me.rutrackersearch.domain.repository.BookmarksRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveBookmarksUseCase @Inject constructor(
    private val repository: BookmarksRepository,
) {
    operator fun invoke(): Flow<List<CategoryModel>> {
        return repository.observeBookmarks()
    }
}
