package flow.domain.usecase

import flow.data.api.repository.SuggestsRepository
import flow.models.search.Suggest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveSuggestsUseCase @Inject constructor(
    private val suggestsRepository: SuggestsRepository,
) {
    operator fun invoke(query: String): Flow<List<Suggest>> {
        return suggestsRepository.observeSuggests()
            .map { suggests ->
                if (query.isBlank()) {
                    suggests.map(::Suggest)
                } else {
                    suggests
                        .filter { suggest ->
                            suggest != query && suggest.contains(query, ignoreCase = true)
                        }
                        .map { suggest ->
                            val substringStart = suggest.indexOf(query, ignoreCase = true)
                            Suggest(suggest, substringStart until (substringStart + query.length))
                        }
                }
            }
    }
}
