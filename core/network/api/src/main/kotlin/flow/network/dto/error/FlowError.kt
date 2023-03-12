package flow.network.dto.error

sealed class FlowError(cause: Throwable? = null) : Throwable(cause) {
    object BadRequest : FlowError()
    object Forbidden : FlowError()
    object NoData : FlowError()
    object NoConnection : FlowError()
    object NotFound : FlowError()
    object Unauthorized : FlowError()
    class Unknown(cause: Throwable? = null) : FlowError(cause)
}
