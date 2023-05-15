package flow.domain.usecase

import flow.auth.api.AuthService
import flow.models.auth.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val authService: AuthService
) {
    operator fun invoke(): Flow<AuthState> {
        return authService.observeAuthState()
            .distinctUntilChanged()
    }
}
