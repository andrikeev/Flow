package flow.account

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.LogoutUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.logger.api.LoggerFactory
import flow.models.auth.AuthState
import kotlinx.coroutines.flow.collectLatest
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
            AccountAction.ConfirmLogoutClick -> onConfirmLogoutClick()
            AccountAction.LoginClick -> onLoginClick()
            AccountAction.LogoutClick -> onLogoutClick()
        }
    }

    private fun observeAuthState() = intent {
        logger.d { "Start observing auth state" }
        observeAuthStateUseCase().collectLatest { authState ->
            logger.d { "On new auth state: $authState" }
            reduce { authState }
        }
    }

    private fun onConfirmLogoutClick() = intent {
        logoutUseCase()
    }

    private fun onLoginClick() = intent {
        postSideEffect(AccountSideEffect.OpenLogin)
    }

    private fun onLogoutClick() = intent {
        postSideEffect(AccountSideEffect.ShowLogoutConfirmation)
    }
}
