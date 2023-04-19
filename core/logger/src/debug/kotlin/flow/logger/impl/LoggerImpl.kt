package flow.logger.impl

import android.util.Log
import flow.logger.api.Logger

internal class LoggerImpl(
    private val tag: String,
) : Logger {
    override fun i(message: () -> String) {
        Log.i(tag, message())
    }

    override fun d(message: () -> String) {
        Log.d(tag, message())
    }

    override fun d(t: Throwable?, message: () -> String) {
        Log.d(tag, message(), t)
    }

    override fun e(message: () -> String) {
        Log.e(tag, message())
    }

    override fun e(t: Throwable?, message: () -> String) {
        Log.e(tag, message(), t)
    }
}
