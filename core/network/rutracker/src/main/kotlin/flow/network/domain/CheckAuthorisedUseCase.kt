package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.ResultDto

internal class CheckAuthorisedUseCase(
    private val api: RuTrackerInnerApi,
    private val verifyAuthorisedUseCase: VerifyAuthorisedUseCase,
) {
    suspend operator fun invoke(token: String): ResultDto<Boolean> = tryCatching {
        verifyAuthorisedUseCase(api.mainPage(token)).toResult()
    }
}
