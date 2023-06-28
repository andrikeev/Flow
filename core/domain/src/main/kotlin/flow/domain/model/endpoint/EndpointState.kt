package flow.domain.model.endpoint

import flow.models.settings.Endpoint

data class EndpointState(
    val endpoint: Endpoint,
    val selected: Boolean,
    val status: EndpointStatus,
)
