package flow.connection

import flow.models.settings.Endpoint

internal sealed interface ConnectionsAction {
    object ClickConnectionItem : ConnectionsAction
    data class SelectEndpoint(val endpoint: Endpoint) : ConnectionsAction
}
