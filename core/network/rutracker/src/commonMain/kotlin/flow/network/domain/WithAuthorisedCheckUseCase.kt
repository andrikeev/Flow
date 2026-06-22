package flow.network.domain

import flow.network.model.Unauthorized

internal class WithAuthorisedCheckUseCase(
    private val verifyAuthorisedUseCase: VerifyAuthorisedUseCase,
) {
    suspend operator fun <T> invoke(
        html: String,
        mapper: suspend (html: String) -> T,
    ): T {
        return if (verifyAuthorisedUseCase(html)) {
            mapper(html)
        } else {
            throw Unauthorized
        }
    }
}
