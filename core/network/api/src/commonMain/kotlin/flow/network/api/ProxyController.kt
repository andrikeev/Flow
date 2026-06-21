package flow.network.api

/**
 * Applies the user configured proxy to all outgoing network requests.
 *
 * Call [setup] once on application start to begin observing proxy settings.
 */
interface ProxyController {
    fun setup()
}
