package flow.topic.torrent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.common.newCancelableScope
import flow.common.relaunch
import flow.domain.usecase.EnrichTopicUseCase
import flow.domain.usecase.EnrichTorrentUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
import flow.domain.usecase.VisitTopicUseCase
import flow.logger.api.LoggerFactory
import flow.models.auth.isAuthorized
import flow.models.search.Filter
import flow.models.topic.TopicModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class TorrentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val enrichTopicUseCase: EnrichTopicUseCase,
    private val enrichTorrentUseCase: EnrichTorrentUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val visitTopicUseCase: VisitTopicUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<TorrentState, TorrentSideEffect> {
    private val logger = loggerFactory.get("TorrentViewModel")
    private val observeTorrentScope = viewModelScope.newCancelableScope()

    override val container: Container<TorrentState, TorrentSideEffect> = container(
        initialState = TorrentState(TopicModel(savedStateHandle.torrent)),
        onCreate = { state ->
            viewModelScope.launch { visitTopicUseCase(state.torrent.topic) }
            viewModelScope.launch {
                observeAuthStateUseCase().collectLatest { authState ->
                    intent { reduce { state.copy(isAuthorised = authState.isAuthorized) } }
                }
            }
            loadTorrent()
        },
    )

    fun perform(action: TorrentAction) {
        when (action) {
            is TorrentAction.AuthorClick -> onAuthorClick()
            is TorrentAction.BackClick -> onBackClick()
            is TorrentAction.CategoryClick -> onCategoryClick()
            is TorrentAction.CommentsClick -> onCommentsClick()
            is TorrentAction.FavoriteClick -> onFavoriteClick()
            is TorrentAction.MagnetClick -> onMagnetClick()
            is TorrentAction.RetryClick -> loadTorrent()
            is TorrentAction.ShareClick -> onShareClick()
            is TorrentAction.TorrentFileClick -> onTorrentFileClick()
        }
    }

    private fun onAuthorClick() = intent {
        postSideEffect(TorrentSideEffect.OpenSearch(Filter(author = state.torrent.topic.author)))
    }

    private fun onBackClick() = intent { postSideEffect(TorrentSideEffect.Back) }

    private fun onCategoryClick() = intent {
        postSideEffect(TorrentSideEffect.OpenCategory(state.torrent.topic.category?.id!!)) //FIXME
    }

    private fun onCommentsClick() = intent {
        postSideEffect(TorrentSideEffect.OpenComments(state.torrent.topic))
    }

    private fun onFavoriteClick() = intent { toggleFavoriteUseCase(state.torrent.topic.id) }

    private fun onMagnetClick() = intent {
        state.torrent.topic.magnetLink?.let { link ->
            postSideEffect(TorrentSideEffect.OpenMagnet(link))
        }
    }

    private fun onShareClick() = intent {
        val link = "https://rutracker.org/forum/viewtopic.php?t=${state.torrent.topic.id}"
        postSideEffect(TorrentSideEffect.Share(link))
    }

    private fun onTorrentFileClick() = intent {
        postSideEffect(TorrentSideEffect.Download(state.torrent.topic))
    }

    private fun loadTorrent() = intent {
        reduce { state.copy(isLoading = true, error = null) }
        observeTorrentScope.relaunch {
            runCatching { enrichTorrentUseCase(state.torrent.topic) }
                .onSuccess { torrent ->
                    logger.d { "Torrent loaded" }
                    enrichTopicUseCase(torrent).collectLatest { topicModel ->
                        reduce { state.copy(torrent = topicModel, isLoading = false) }
                    }
                }
                .onFailure { error ->
                    logger.e(error) { "Torrent load error" }
                    reduce { state.copy(isLoading = false, error = error) }
                }
        }
    }
}
