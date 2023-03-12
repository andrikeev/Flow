package flow.network.api

import flow.network.impl.ProxyInnerApiImpl
import flow.network.impl.ProxyNetworkApi
import io.ktor.client.*

object ProxyApiFactory {
    fun create(httpClient: HttpClient): NetworkApi {
        return ProxyNetworkApi(ProxyInnerApiImpl(httpClient))
    }
}
