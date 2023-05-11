package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.user.ProfileDto
import org.jsoup.Jsoup

internal class GetCurrentProfileUseCase(
    private val api: RuTrackerInnerApi,
    private val getProfileUseCase: GetProfileUseCase,
) {
    suspend operator fun invoke(token: String): ProfileDto {
        return getProfileUseCase(parseUserId(api.mainPage(token)))
    }

    companion object {
        private fun parseUserId(html: String): String {
            return Jsoup.parse(html)
                .select("#logged-in-username")
                .queryParam("u")
        }
    }
}
