package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.user.ProfileDto

internal class GetCurrentProfileUseCase(
    private val api: RuTrackerInnerApi,
    private val getProfileUseCase: GetProfileUseCase,
    private val parser: RuTrackerParser,
) {
    suspend operator fun invoke(token: String): ProfileDto {
        return getProfileUseCase(parser.parseCurrentUserId(api.mainPage(token)))
    }
}
