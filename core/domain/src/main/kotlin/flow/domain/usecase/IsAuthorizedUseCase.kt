package flow.domain.usecase

import flow.auth.api.AuthService
import javax.inject.Inject

class IsAuthorizedUseCase @Inject constructor(
    private val authService: AuthService
) {
    operator fun invoke(): Boolean = authService.isAuthorized()
}
