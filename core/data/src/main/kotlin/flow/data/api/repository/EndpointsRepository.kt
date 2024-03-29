package flow.data.api.repository

import flow.models.settings.Endpoint
import kotlinx.coroutines.flow.Flow

interface EndpointsRepository {
    suspend fun observeAll(): Flow<List<Endpoint>>
    suspend fun add(endpoint: Endpoint)
    suspend fun remove(endpoint: Endpoint)
}
