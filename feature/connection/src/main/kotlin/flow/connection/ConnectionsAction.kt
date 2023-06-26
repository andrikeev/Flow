package flow.connection

import flow.models.settings.Endpoint

internal sealed interface ConnectionsAction {
    object ConnectionItemClick : ConnectionsAction
    object EditClick : ConnectionsAction
    object DoneClick : ConnectionsAction
    data class SelectEndpoint(val endpoint: Endpoint) : ConnectionsAction
    data class SubmitEndpoint(val endpoint: String) : ConnectionsAction
    data class RemoveEndpoint(val endpoint: Endpoint) : ConnectionsAction
}
