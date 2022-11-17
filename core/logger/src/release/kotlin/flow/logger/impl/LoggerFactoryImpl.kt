package flow.logger.impl

import flow.logger.api.Logger
import flow.logger.api.LoggerFactory
import javax.inject.Inject

internal class LoggerFactoryImpl @Inject constructor() : LoggerFactory {
    override fun get(tag: String): Logger = StubLogger
}
