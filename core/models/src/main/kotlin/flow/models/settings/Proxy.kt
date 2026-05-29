package flow.models.settings

data class Proxy(
    val enabled: Boolean = false,
    val type: ProxyType = ProxyType.HTTP,
    val host: String = "",
    val port: Int = 0,
    val username: String = "",
    val password: String = "",
)

enum class ProxyType {
    HTTP,
    SOCKS,
}
