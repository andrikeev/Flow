package flow.models.settings

sealed interface Endpoint {
    val host: String

    object Proxy : Endpoint {
        override val host: String = "flow-proxy-m7o3b.ondigitalocean.app"
    }

    sealed interface RutrackerEndpoint : Endpoint

    object Rutracker : RutrackerEndpoint {
        override val host: String = "rutracker.org"
    }

    data class Mirror(override val host: String) : RutrackerEndpoint

    companion object
}
