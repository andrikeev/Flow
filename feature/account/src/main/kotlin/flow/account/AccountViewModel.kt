package flow.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.LogoutUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.models.user.AuthState
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
) : ViewModel(), ContainerHost<AuthState, AccountSideEffect> {
    override val container: Container<AuthState, AccountSideEffect> = container(
        initialState = AuthState.Unauthorized,
        onCreate = { observeAuthState() },
    )

    fun perform(action: AccountAction) = intent {
        when (action) {
            AccountAction.CancelLogoutClick -> postSideEffect(AccountSideEffect.HideLogoutConfirmation)
            AccountAction.ConfirmLogoutClick -> logoutUseCase()
            AccountAction.LoginClick -> postSideEffect(AccountSideEffect.OpenLogin)
            AccountAction.LogoutClick -> postSideEffect(AccountSideEffect.ShowLogoutConfirmation)
        }
    }

    private fun observeAuthState() = intent {
        viewModelScope.launch {
            observeAuthStateUseCase().collectLatest { authState ->
                reduce { authState }
            }
        }
    }
}
