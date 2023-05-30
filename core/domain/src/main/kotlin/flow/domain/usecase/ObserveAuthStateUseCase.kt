package flow.domain.usecase

import flow.auth.api.AuthService
import flow.models.auth.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

interface ObserveAuthStateUseCase : () -> Flow<AuthState>

class ObserveAuthStateUseCaseImpl @Inject constructor(
    private val authService: AuthService
) : ObserveAuthStateUseCase {
    override operator fun invoke(): Flow<AuthState> {
        return authService.observeAuthState()
            .distinctUntilChanged()
    }
}
