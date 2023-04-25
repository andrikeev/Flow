package flow.topic.torrent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.EnrichTopicUseCase
import flow.domain.usecase.EnrichTorrentUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
import flow.domain.usecase.VisitTopicUseCase
import flow.logger.api.LoggerFactory
import flow.models.search.Filter
import kotlinx.coroutines.coroutineScope
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
) : ViewModel(), ContainerHost<TorrentScreenState, TorrentSideEffect> {
    private val logger = loggerFactory.get("TorrentViewModel")
    private val torrent = savedStateHandle.torrent

    override val container: Container<TorrentScreenState, TorrentSideEffect> = container(
        initialState = TorrentScreenState(torrentState = TorrentState.Initial(torrent)),
        onCreate = {
            viewModelScope.launch {
                observeAuthStateUseCase().collectLatest { authState ->
                    intent { reduce { state.copy(authState = authState) } }
                }
            }
            viewModelScope.launch {
                enrichTopicUseCase(torrent).collectLatest { topicModel ->
                    intent {
                        reduce {
                            state.copy(favoriteState = TorrentFavoriteState.FavoriteState(topicModel.isFavorite))
                        }
                    }
                }
            }
            viewModelScope.launch {
                visitTopicUseCase(torrent)
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
        postSideEffect(TorrentSideEffect.OpenSearch(Filter(author = torrent.author)))
    }

    private fun onBackClick() = intent { postSideEffect(TorrentSideEffect.Back) }

    private fun onCategoryClick() = intent {
        torrent.category?.id?.let { categoryId ->
            postSideEffect(TorrentSideEffect.OpenCategory(categoryId))
        }
    }

    private fun onCommentsClick() = intent {
        postSideEffect(TorrentSideEffect.OpenComments(torrent))
    }

    private fun onFavoriteClick() = intent { toggleFavoriteUseCase(torrent.id) }

    private fun onMagnetClick() = intent {
        torrent.magnetLink?.let { link ->
            postSideEffect(TorrentSideEffect.OpenMagnet(link))
        }
    }

    private fun onShareClick() = intent {
        val link = "https://rutracker.org/forum/viewtopic.php?t=${torrent.id}"
        postSideEffect(TorrentSideEffect.Share(link))
    }

    private fun onTorrentFileClick() = intent {
        postSideEffect(TorrentSideEffect.Download(torrent))
    }

    private fun loadTorrent() {
        intent { reduce { state.copy(torrentState = TorrentState.Initial(state.torrentState.torrent)) } }
        viewModelScope.launch {
            runCatching {
                coroutineScope {
                    enrichTorrentUseCase(torrent)
                }
            }
                .onSuccess { torrent ->
                    logger.d { "Torrent loaded" }
                    intent {
                        reduce {
                            state.copy(torrentState = TorrentState.Loaded(torrent))
                        }
                    }
                }
                .onFailure { error ->
                    logger.e(error) { "Torrent load error" }
                    intent {
                        reduce {
                            state.copy(torrentState = TorrentState.Error(state.torrentState.torrent))
                        }
                    }
                }
        }
    }
}
