package flow.domain.usecase

import flow.auth.api.AuthService

class IsAuthorizedUseCase(
    private val authService: AuthService,
) {
    suspend operator fun invoke(): Boolean = authService.isAuthorized()
}
