package flow.network.domain

import flow.network.api.RuTrackerInnerApi

internal class CheckAuthorisedUseCase(
    private val api: RuTrackerInnerApi,
    private val verifyAuthorisedUseCase: VerifyAuthorisedUseCase,
) {
    suspend operator fun invoke(token: String): Boolean {
        return verifyAuthorisedUseCase(api.mainPage(token))
    }
}
