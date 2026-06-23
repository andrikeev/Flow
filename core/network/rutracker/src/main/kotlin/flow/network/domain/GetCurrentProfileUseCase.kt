package flow.network.domain

import com.fleeksoft.ksoup.Ksoup
import flow.network.api.RuTrackerInnerApi
import flow.network.dto.user.ProfileDto

internal class GetCurrentProfileUseCase(
    private val api: RuTrackerInnerApi,
    private val getProfileUseCase: GetProfileUseCase,
) {
    suspend operator fun invoke(token: String): ProfileDto {
        return getProfileUseCase(parseUserId(api.mainPage(token)))
    }

    companion object {
        private fun parseUserId(html: String): String {
            return Ksoup.parse(html)
                .select("#logged-in-username")
                .queryParam("u")
        }
    }
}
