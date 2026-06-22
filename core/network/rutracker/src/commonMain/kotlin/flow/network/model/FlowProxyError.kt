package flow.network.model

sealed class FlowProxyError : Throwable()

data object BadRequest : FlowProxyError()
data object Forbidden : FlowProxyError()
data object NoData : FlowProxyError()
data object NoConnection : FlowProxyError()
data object NotFound : FlowProxyError()
data object Unauthorized : FlowProxyError()
data object Unknown : FlowProxyError()
