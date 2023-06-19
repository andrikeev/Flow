package flow.connection

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.model.endpoint.EndpointState
import flow.domain.usecase.ObserveEndpointsStatusUseCase
import flow.domain.usecase.SetEndpointUseCase
import flow.models.settings.Endpoint
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class ConnectionsViewModel @Inject constructor(
    private val setEndpointUseCase: SetEndpointUseCase,
    observeEndpointsStatusUseCase: ObserveEndpointsStatusUseCase,
) : ViewModel(), ContainerHost<List<EndpointState>, ConnectionsSideEffect> {
    override val container: Container<List<EndpointState>, ConnectionsSideEffect> = container(
        initialState = emptyList(),
        onCreate = { intent { observeEndpointsStatusUseCase().collectLatest { reduce { it } } } },
    )

    fun perform(action: ConnectionsAction) {
        when (action) {
            is ConnectionsAction.ClickConnectionItem -> onClickConnectionItem()
            is ConnectionsAction.SelectEndpoint -> onSelectEndpoint(action.endpoint)
        }
    }

    private fun onClickConnectionItem() = intent {
        postSideEffect(ConnectionsSideEffect.ShowConnectionDialog)
    }

    private fun onSelectEndpoint(endpoint: Endpoint) = intent {
        setEndpointUseCase(endpoint)
    }
}
