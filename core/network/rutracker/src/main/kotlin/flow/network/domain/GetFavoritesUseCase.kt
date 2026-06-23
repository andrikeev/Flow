package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.user.FavoritesDto

internal class GetFavoritesUseCase(
    private val api: RuTrackerInnerApi,
    private val withTokenVerificationUseCase: WithTokenVerificationUseCase,
    private val withAuthorisedCheckUseCase: WithAuthorisedCheckUseCase,
    private val parser: RuTrackerParser,
) {

    suspend operator fun invoke(token: String): FavoritesDto {
        return withTokenVerificationUseCase(token) { validToken ->
            withAuthorisedCheckUseCase(api.favorites(validToken, 1)) { html ->
                val pagesCount = parser.parseFavoritesPagesCount(html)
                FavoritesDto(
                    (
                        listOf(parser.parseFavorites(html)) +
                            (2..pagesCount)
                                .map { page -> api.favorites(token, page) }
                                .map(parser::parseFavorites)
                        )
                        .flatten(),
                )
            }
        }
    }
}
