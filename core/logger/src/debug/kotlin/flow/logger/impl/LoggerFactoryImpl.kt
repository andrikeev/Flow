package flow.logger.impl

import flow.logger.api.Logger
import flow.logger.api.LoggerFactory

internal class LoggerFactoryImpl : LoggerFactory {
    override fun get(tag: String): Logger = LoggerImpl(tag)
}
