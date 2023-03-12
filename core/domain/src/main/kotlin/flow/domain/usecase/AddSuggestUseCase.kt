package flow.domain.usecase

import flow.data.api.repository.SuggestsRepository
import javax.inject.Inject

class AddSuggestUseCase @Inject constructor(
    private val suggestsRepository: SuggestsRepository,
) {
    suspend operator fun invoke(query: String?) {
        if (!query.isNullOrBlank()) {
            suggestsRepository.addSuggest(query)
        }
    }
}
