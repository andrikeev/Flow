package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.repository.SuggestsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSuggestUseCase @Inject constructor(
    private val suggestsRepository: SuggestsRepository,
) {
    suspend operator fun invoke(query: String?) {
        if (!query.isNullOrBlank()) {
            suggestsRepository.addSuggest(query)
        }
    }
}
