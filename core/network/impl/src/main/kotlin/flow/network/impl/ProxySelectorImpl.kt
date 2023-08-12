package flow.network.impl

import java.io.IOException
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI
import javax.inject.Inject

/**
 * @see <a href=https://github.com/square/okhttp/issues/6877#issuecomment-1438554879>workaround</>
 */
class DelegatingProxySelector @Inject constructor() : ProxySelector() {
    override fun select(uri: URI?): List<Proxy> {
        return runCatching { getDefault().select(uri) }
            .getOrElse { listOf(Proxy.NO_PROXY) }
    }

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
        getDefault().connectFailed(uri, sa, ioe)
    }
}
