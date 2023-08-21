package flow.connection

import flow.models.settings.Endpoint

internal sealed interface ConnectionsAction {
    data object ConnectionItemClick : ConnectionsAction
    data object EditClick : ConnectionsAction
    data object DoneClick : ConnectionsAction
    data class SelectEndpoint(val endpoint: Endpoint) : ConnectionsAction
    data class SubmitEndpoint(val endpoint: String) : ConnectionsAction
    data class RemoveEndpoint(val endpoint: Endpoint) : ConnectionsAction
}
