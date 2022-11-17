package flow.logger.impl

import flow.logger.api.Logger

internal object StubLogger : Logger {
    override fun d(message: () -> String) = Unit
    override fun d(t: Throwable?, message: () -> String) = Unit
    override fun e(message: () -> String) = Unit
    override fun e(t: Throwable?, message: () -> String) = Unit
}
