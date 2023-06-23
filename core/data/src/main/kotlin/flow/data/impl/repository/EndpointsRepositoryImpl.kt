package flow.data.impl.repository

import flow.data.api.repository.EndpointsRepository
import flow.data.converters.toEntity
import flow.data.converters.toModel
import flow.database.dao.EndpointDao
import flow.database.entity.EndpointEntity
import flow.models.settings.Endpoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class EndpointsRepositoryImpl @Inject constructor(
    private val endpointDao: EndpointDao,
) : EndpointsRepository {
    override suspend fun observeAll(): Flow<List<Endpoint>> {
        return endpointDao
            .observerAll()
            .onStart {
                runCatching {
                    if (endpointDao.isEmpty()) {
                        endpointDao.insertAll(defaultEndpoints)
                    }
                }
            }
            .mapLatest { entities ->
                entities.mapNotNull(EndpointEntity::toModel)
            }
    }

    private companion object {
        val defaultEndpoints: List<EndpointEntity> by lazy {
            listOf(
                Endpoint.Proxy,
                Endpoint.Rutracker,
            ).map(Endpoint::toEntity)
        }
    }
}
