package flow.topic.torrent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.DownloadTorrentUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveFavoriteStateUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
import flow.logger.api.LoggerFactory
import flow.models.auth.isAuthorized
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Author
import flow.topic.open.id
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class TorrentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val downloadTorrentUseCase: DownloadTorrentUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val observeFavoriteStateUseCase: ObserveFavoriteStateUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<TorrentScreenState, TorrentSideEffect> {
    private val logger = loggerFactory.get("TorrentViewModel")
    private val id = savedStateHandle.id

    override val container: Container<TorrentScreenState, TorrentSideEffect> = container(
        initialState = TorrentScreenState(),
        onCreate = { state ->
            viewModelScope.launch {
                observeFavoriteStateUseCase(id).collectLatest { isFavorite ->
                    val favoriteState = TorrentFavoriteState.FavoriteState(isFavorite)
                    intent { reduce { state.copy(favoriteState = favoriteState) } }
                }
            }
        },
    )

    fun perform(action: TorrentAction) {
        logger.d { "Perform $action" }
        when (action) {
            is TorrentAction.AuthorClick -> onAuthorClick(action.author)
            is TorrentAction.BackClick -> onBackClick()
            is TorrentAction.CategoryClick -> onCategoryClick(action.category)
            is TorrentAction.CommentsClick -> onCommentsClick()
            is TorrentAction.FavoriteClick -> onFavoriteClick()
            is TorrentAction.MagnetClick -> onMagnetClick(action.link)
            is TorrentAction.ShareClick -> onShareClick()
            is TorrentAction.TorrentFileClick -> onTorrentFileClick(action.title)
            is TorrentAction.LoginClick -> onLoginClick()
            is TorrentAction.OpenFileClick -> onOpenFileClick(action.uri)
        }
    }

    private fun onAuthorClick(author: Author) = intent {
        postSideEffect(TorrentSideEffect.OpenSearch(Filter(author = author)))
    }

    private fun onBackClick() = intent {
        postSideEffect(TorrentSideEffect.Back)
    }

    private fun onCategoryClick(category: Category) = intent {
        postSideEffect(TorrentSideEffect.OpenCategory(category.id))
    }

    private fun onCommentsClick() = intent {
        postSideEffect(TorrentSideEffect.OpenComments(id))
    }

    private fun onFavoriteClick() = viewModelScope.launch {
        toggleFavoriteUseCase(id)
    }

    private fun onMagnetClick(link: String) = intent {
        postSideEffect(TorrentSideEffect.ShowMagnet(link))
    }

    private fun onShareClick() = intent {
        postSideEffect(TorrentSideEffect.ShareLink(createShareLink()))
    }

    private fun onTorrentFileClick(title: String) = intent {
        if (observeAuthStateUseCase().firstOrNull().isAuthorized) {
            postSideEffect(TorrentSideEffect.ShowDownloadProgress)
            reduce { state.copy(downloadState = DownloadState.Started) }
            val uri = downloadTorrentUseCase(id, title)
            if (uri != null) {
                intent { reduce { state.copy(downloadState = DownloadState.Completed(uri)) } }
            } else {
                intent { reduce { state.copy(downloadState = DownloadState.Error) } }
            }
        } else {
            intent { postSideEffect(TorrentSideEffect.ShowLoginRequest) }
        }
    }

    private fun onLoginClick() = intent {
        postSideEffect(TorrentSideEffect.OpenLogin)
    }

    private fun onOpenFileClick(uri: String) = intent {
        postSideEffect(TorrentSideEffect.OpenFile(uri))
    }

    private fun createShareLink(): String {
        return "https://rutracker.org/forum/viewtopic.php?t=$id"
    }
}
