package flow.network.data

import flow.network.api.NetworkApi

interface NetworkApiRepository {
    suspend fun getAvailableApiList(): Collection<NetworkApi>
    suspend fun getCurrentApi(): NetworkApi
}
