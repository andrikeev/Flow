package flow.domain.usecase

import flow.data.api.repository.SuggestsRepository
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext

class AddSuggestUseCase(
    private val suggestsRepository: SuggestsRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(query: String?) {
        withContext(dispatchers.default) {
            if (!query.isNullOrBlank()) {
                suggestsRepository.addSuggest(query)
            }
        }
    }
}
