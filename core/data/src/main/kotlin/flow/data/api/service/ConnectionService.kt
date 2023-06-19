package flow.data.api.service

import kotlinx.coroutines.flow.Flow

interface ConnectionService {
    val networkUpdates: Flow<Boolean>
    suspend fun isReachable(host: String): Boolean
}
