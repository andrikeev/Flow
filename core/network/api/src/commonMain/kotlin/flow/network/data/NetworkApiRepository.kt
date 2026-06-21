package flow.network.data

import flow.network.api.NetworkApi

interface NetworkApiRepository {
    suspend fun getApi(): NetworkApi
    suspend fun getCaptchaUrl(url: String): String
    suspend fun getDownloadUri(id: String): String
    suspend fun getAuthHeader(token: String): Pair<String, String>
}
