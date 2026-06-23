package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.user.ProfileDto

internal class GetProfileUseCase(
    private val api: RuTrackerInnerApi,
    private val parser: RuTrackerParser,
) {
    suspend operator fun invoke(id: String): ProfileDto {
        return parser.parseProfile(api.profile(id))
    }
}
