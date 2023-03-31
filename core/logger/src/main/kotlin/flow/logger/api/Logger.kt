package flow.logger.api

interface Logger {
    fun i(message: () -> String)
    fun d(message: () -> String)
    fun d(t: Throwable? = null, message: () -> String)
    fun e(message: () -> String)
    fun e(t: Throwable? = null, message: () -> String)
}
