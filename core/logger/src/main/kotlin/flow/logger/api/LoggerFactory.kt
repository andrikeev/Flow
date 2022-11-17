package flow.logger.api

interface LoggerFactory {
    fun get(tag: String): Logger
}
