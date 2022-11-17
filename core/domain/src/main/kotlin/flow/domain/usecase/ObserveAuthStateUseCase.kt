package flow.domain.usecase

import flow.auth.api.AuthRepository
import flow.models.user.AuthState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<AuthState> = authRepository.observeAuthState()
}
