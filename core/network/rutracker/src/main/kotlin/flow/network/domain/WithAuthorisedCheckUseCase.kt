package flow.network.domain

import flow.network.dto.ResultDto
import flow.network.dto.error.FlowError

internal class WithAuthorisedCheckUseCase(
    private val verifyAuthorisedUseCase: VerifyAuthorisedUseCase,
) {
    suspend operator fun <T> invoke(
        html: String,
        mapper: suspend (html: String) -> ResultDto<T>,
    ): ResultDto<T> = if (verifyAuthorisedUseCase(html)) {
        mapper(html)
    } else {
        ResultDto.Error(FlowError.Unauthorized)
    }
}
