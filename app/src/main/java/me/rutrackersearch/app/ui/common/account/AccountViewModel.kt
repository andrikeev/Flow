package me.rutrackersearch.app.ui.common.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.rutrackersearch.domain.usecase.LogoutUseCase
import me.rutrackersearch.domain.usecase.ObserveAuthStateUseCase
import me.rutrackersearch.models.user.AuthState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
) : ViewModel(), ContainerHost<AuthState, AccountSideEffect> {
    override val container: Container<AuthState, AccountSideEffect> = container(
        initialState = AuthState.Unauthorized,
        onCreate = { observeAuthState() },
    )

    fun perform(action: AccountAction) {
        when (action) {
            AccountAction.LoginClick -> intent { postSideEffect(AccountSideEffect.OpenLogin) }
            AccountAction.LogoutClick -> viewModelScope.launch { logoutUseCase() }
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            observeAuthStateUseCase().collectLatest { authState -> intent { reduce { authState } } }
        }
    }
}
