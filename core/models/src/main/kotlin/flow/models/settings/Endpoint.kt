package flow.models.settings

sealed interface Endpoint {
    val host: String

    data object Proxy : Endpoint {
        override val host: String = "flow-app.tech"
    }

    sealed interface RutrackerEndpoint : Endpoint

    data object Rutracker : RutrackerEndpoint {
        override val host: String = "rutracker.org"
    }

    data class Mirror(override val host: String) : RutrackerEndpoint

    companion object
}
