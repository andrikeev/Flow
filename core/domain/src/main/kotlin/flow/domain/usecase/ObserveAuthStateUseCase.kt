package flow.domain.usecase

import flow.auth.api.AuthService
import flow.models.auth.AuthState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val authService: AuthService
) {
    operator fun invoke(): Flow<AuthState> = authService.observeAuthState()
}
