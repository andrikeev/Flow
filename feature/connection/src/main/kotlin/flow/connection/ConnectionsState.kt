package flow.connection

import flow.domain.model.endpoint.EndpointState

data class ConnectionsState(
    val selected: EndpointState? = null,
    val connections: List<EndpointState> = emptyList(),
    val edit: Boolean = false,
)
