package flow.network.api

import flow.network.domain.AddCommentUseCase
import flow.network.domain.AddFavoriteUseCase
import flow.network.domain.CheckAuthorisedUseCase
import flow.network.domain.GetCategoryPageUseCase
import flow.network.domain.GetCommentsPageUseCase
import flow.network.domain.GetCurrentProfileUseCase
import flow.network.domain.GetFavoritesUseCase
import flow.network.domain.GetForumUseCase
import flow.network.domain.GetProfileUseCase
import flow.network.domain.GetSearchPageUseCase
import flow.network.domain.GetTopicPageUseCase
import flow.network.domain.GetTopicUseCase
import flow.network.domain.GetTorrentFileUseCase
import flow.network.domain.GetTorrentUseCase
import flow.network.domain.LoginUseCase
import flow.network.domain.RemoveFavoriteUseCase
import flow.network.domain.RuTrackerHtmlParser
import flow.network.domain.VerifyAuthorisedUseCase
import flow.network.domain.VerifyTokenUseCase
import flow.network.domain.WithAuthorisedCheckUseCase
import flow.network.domain.WithFormTokenUseCase
import flow.network.domain.WithTokenVerificationUseCase
import flow.network.impl.RuTrackerInnerApiImpl
import flow.network.impl.RuTrackerNetworkApi
import io.ktor.client.HttpClient

object RuTrackerApiFactory {
    fun create(httpClient: HttpClient): NetworkApi {
        val api = RuTrackerInnerApiImpl(httpClient)
        val parser = RuTrackerHtmlParser()
        val withTokenVerification = WithTokenVerificationUseCase(VerifyTokenUseCase)
        val withAuthorisedCheck = WithAuthorisedCheckUseCase(VerifyAuthorisedUseCase)
        return RuTrackerNetworkApi(
            AddCommentUseCase(api, withTokenVerification, withAuthorisedCheck, WithFormTokenUseCase),
            AddFavoriteUseCase(api, withTokenVerification, withAuthorisedCheck, WithFormTokenUseCase),
            CheckAuthorisedUseCase(api, VerifyAuthorisedUseCase),
            GetCategoryPageUseCase(api, parser),
            GetCommentsPageUseCase(api, parser),
            GetFavoritesUseCase(api, withTokenVerification, withAuthorisedCheck, parser),
            GetForumUseCase(api, parser),
            GetSearchPageUseCase(api, withTokenVerification, withAuthorisedCheck, parser),
            GetTopicUseCase(api, parser),
            GetTopicPageUseCase(api, parser),
            GetTorrentFileUseCase(api, withTokenVerification),
            GetTorrentUseCase(api, parser),
            LoginUseCase(api, GetCurrentProfileUseCase(api, GetProfileUseCase(api, parser), parser)),
            RemoveFavoriteUseCase(api, withTokenVerification, withAuthorisedCheck, WithFormTokenUseCase),
        )
    }
}
