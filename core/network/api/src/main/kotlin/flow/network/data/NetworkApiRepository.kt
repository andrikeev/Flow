package flow.network.data

import flow.network.api.NetworkApi

interface NetworkApiRepository {
    suspend fun getApi(): NetworkApi
    suspend fun getDownloadUri(id: String): String
    suspend fun getAuthHeader(token: String): Pair<String, String>
}
