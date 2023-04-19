package flow.network.data

import flow.logger.api.LoggerFactory
import io.ktor.client.plugins.logging.Logger
import javax.inject.Inject

internal class NetworkLogger @Inject constructor(loggerFactory: LoggerFactory) : Logger {
    private val logger = loggerFactory.get("NetworkLogger")
    override fun log(message: String) = logger.i { message }
}
