package flow.network.domain

import flow.network.model.Unauthorized

internal class WithTokenVerificationUseCase(
    private val verifyTokenUseCase: VerifyTokenUseCase,
) {
    suspend operator fun <T> invoke(
        token: String,
        block: suspend (validToken: String) -> T,
    ): T {
        return if (verifyTokenUseCase(token)) {
            block(token)
        } else {
            throw Unauthorized
        }
    }
}
