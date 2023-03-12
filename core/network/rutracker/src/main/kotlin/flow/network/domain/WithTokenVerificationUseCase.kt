package flow.network.domain

import flow.network.dto.ResultDto
import flow.network.dto.error.FlowError

internal class WithTokenVerificationUseCase(
    private val verifyTokenUseCase: VerifyTokenUseCase,
) {
    suspend operator fun <T> invoke(
        token: String,
        block: suspend (validToken: String) -> ResultDto<T>
    ): ResultDto<T> {
        return if (verifyTokenUseCase(token)) {
            ResultDto.Error(FlowError.Unauthorized)
        } else {
            block(token)
        }
    }
}
