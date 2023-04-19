package flow.domain.usecase

import flow.dispatchers.api.Dispatchers
import flow.models.search.Filter
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EnrichFilterUseCase @Inject constructor(
    private val getCategoryUseCase: GetCategoryUseCase,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(filter: Filter): Filter {
        return withContext(dispatchers.default) {
            filter.copy(categories = filter.categories?.map { getCategoryUseCase(it.id) })
        }
    }
}
