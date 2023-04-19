package flow.testing.logger

import flow.logger.api.Logger
import flow.logger.api.LoggerFactory

class TestLoggerFactory : LoggerFactory {
    override fun get(tag: String): Logger = StubLogger

    private companion object {
        object StubLogger : Logger {
            override fun i(message: () -> String) = Unit
            override fun d(message: () -> String) = Unit
            override fun d(t: Throwable?, message: () -> String) = Unit
            override fun e(message: () -> String) = Unit
            override fun e(t: Throwable?, message: () -> String) = Unit
        }
    }
}
