package flow.domain.usecase

import flow.data.api.service.StoreService
import flow.dispatchers.api.Dispatchers
import flow.models.Store
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GetRatingStoreUseCase : suspend () -> Store

internal class GetRatingStoreUseCaseImpl @Inject constructor(
    private val storeService: StoreService,
    private val dispatchers: Dispatchers,
) : GetRatingStoreUseCase {
    override suspend fun invoke() = withContext(dispatchers.default) {
        storeService.getStore()
    }
}
