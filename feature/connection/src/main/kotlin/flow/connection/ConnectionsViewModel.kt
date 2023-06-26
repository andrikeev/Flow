package flow.connection

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.model.endpoint.EndpointState
import flow.domain.usecase.AddEndpointUseCase
import flow.domain.usecase.ObserveEndpointsStatusUseCase
import flow.domain.usecase.RemoveEndpointUseCase
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
    private val addEndpointUseCase: AddEndpointUseCase,
    private val removeEndpointUseCase: RemoveEndpointUseCase,
    private val setEndpointUseCase: SetEndpointUseCase,
    private val observeEndpointsStatusUseCase: ObserveEndpointsStatusUseCase,
) : ViewModel(), ContainerHost<ConnectionsState, ConnectionsSideEffect> {
    override val container: Container<ConnectionsState, ConnectionsSideEffect> = container(
        initialState = ConnectionsState(),
        onCreate = { observeConnections() },
    )

    fun perform(action: ConnectionsAction) {
        when (action) {
            is ConnectionsAction.ConnectionItemClick -> onClickConnectionItem()
            is ConnectionsAction.DoneClick -> onDoneClick()
            is ConnectionsAction.EditClick -> onEditClick()
            is ConnectionsAction.RemoveEndpoint -> onRemoveEndpoint(action.endpoint)
            is ConnectionsAction.SelectEndpoint -> onSelectEndpoint(action.endpoint)
            is ConnectionsAction.SubmitEndpoint -> onSubmitEndpoint(action.endpoint)
        }
    }

    private fun onDoneClick() = intent { reduce { state.copy(edit = false) } }

    private fun onEditClick() = intent { reduce { state.copy(edit = true) } }

    private fun observeConnections() = intent {
        observeEndpointsStatusUseCase().collectLatest { connections ->
            reduce {
                state.copy(
                    selected = connections.firstOrNull(EndpointState::selected),
                    connections = connections,
                )
            }
        }
    }

    private fun onClickConnectionItem() = intent {
        postSideEffect(ConnectionsSideEffect.ShowConnectionDialog)
    }

    private fun onRemoveEndpoint(endpoint: Endpoint) = intent {
        removeEndpointUseCase(endpoint)
    }

    private fun onSelectEndpoint(endpoint: Endpoint) = intent {
        setEndpointUseCase(endpoint)
    }

    private fun onSubmitEndpoint(endpoint: String) = intent {
        addEndpointUseCase(endpoint)
        reduce { state.copy(edit = false) }
    }
}
