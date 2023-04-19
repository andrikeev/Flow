package flow.network.model

sealed class FlowProxyError : Throwable()

object BadRequest : FlowProxyError()
object Forbidden : FlowProxyError()
object NoData : FlowProxyError()
object NoConnection : FlowProxyError()
object NotFound : FlowProxyError()
object Unauthorized : FlowProxyError()
object Unknown : FlowProxyError()
