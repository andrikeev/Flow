package flow.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.LogoutUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.logger.api.LoggerFactory
import flow.models.auth.AuthState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class AccountViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<AuthState, AccountSideEffect> {
    private val logger = loggerFactory.get("AccountViewModel")

    override val container: Container<AuthState, AccountSideEffect> = container(
        initialState = AuthState.Unauthorized,
        onCreate = { observeAuthState() },
    )

    fun perform(action: AccountAction) {
        logger.d { "Perform $action" }
        when (action) {
            AccountAction.ConfirmLogoutClick -> viewModelScope.launch { logoutUseCase() }
            AccountAction.LoginClick -> intent { postSideEffect(AccountSideEffect.OpenLogin) }
            AccountAction.LogoutClick -> intent { postSideEffect(AccountSideEffect.ShowLogoutConfirmation) }
        }
    }

    private fun observeAuthState() = viewModelScope.launch {
        logger.d { "Start observing auth state" }
        observeAuthStateUseCase().collectLatest { authState ->
            intent { reduce { authState } }
        }
    }
}
