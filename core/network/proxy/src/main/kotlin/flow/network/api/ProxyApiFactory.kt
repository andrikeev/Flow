package flow.network.api

import flow.network.impl.ProxyNetworkApi
import io.ktor.client.HttpClient

object ProxyApiFactory {
    fun create(httpClient: HttpClient): NetworkApi {
        return ProxyNetworkApi(httpClient)
    }
}
