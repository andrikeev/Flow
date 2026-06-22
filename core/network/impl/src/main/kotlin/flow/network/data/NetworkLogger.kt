package flow.network.data

import flow.logger.api.LoggerFactory
import io.ktor.client.plugins.logging.Logger

internal class NetworkLogger(loggerFactory: LoggerFactory) : Logger {
    private val logger = loggerFactory.get("NetworkLogger")
    override fun log(message: String) = logger.i { message }
}
