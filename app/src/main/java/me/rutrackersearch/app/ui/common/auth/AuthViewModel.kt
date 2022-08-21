package me.rutrackersearch.app.ui.common.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import me.rutrackersearch.models.user.AuthState
import me.rutrackersearch.domain.usecase.LogoutUseCase
import me.rutrackersearch.domain.usecase.ObserveAuthStateUseCase
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    val state: Flow<AuthState> = observeAuthStateUseCase()

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
