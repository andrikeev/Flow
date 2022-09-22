package me.rutrackersearch.app.ui.topic.torrent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.args.requireTorrent
import me.rutrackersearch.domain.usecase.EnrichTopicUseCase
import me.rutrackersearch.domain.usecase.EnrichTorrentUseCase
import me.rutrackersearch.domain.usecase.ObserveAuthStateUseCase
import me.rutrackersearch.domain.usecase.UpdateFavoriteUseCase
import me.rutrackersearch.domain.usecase.VisitTopicUseCase
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.Author
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.user.AuthState
import me.rutrackersearch.utils.newCancelableScope
import me.rutrackersearch.utils.relaunch
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
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val visitTopicUseCase: VisitTopicUseCase,
) : ViewModel(), ContainerHost<TorrentState, TorrentSideEffect> {
    private val observeTorrentScope = newCancelableScope()

    override val container: Container<TorrentState, TorrentSideEffect> = container(
        initialState = TorrentState(TopicModel(savedStateHandle.requireTorrent())),
        onCreate = { state ->
            viewModelScope.launch { visitTopicUseCase(state.torrent.topic) }
            viewModelScope.launch {
                observeAuthStateUseCase().collectLatest { authState ->
                    intent { reduce { state.copy(isAuthorised = authState is AuthState.Authorized) } }
                }
            }
            loadTorrent()
        },
    )

    fun perform(action: TorrentAction) {
        when (action) {
            is TorrentAction.AuthorClick -> onAuthorClick(action.value)
            is TorrentAction.BackClick -> onBackClick()
            is TorrentAction.CategoryClick -> onCategoryClick(action.value)
            is TorrentAction.CommentsClick -> onCommentsClick()
            is TorrentAction.FavoriteClick -> onFavoriteClick()
            is TorrentAction.MagnetClick -> onMagnetClick()
            is TorrentAction.RetryClick -> loadTorrent()
            is TorrentAction.ShareClick -> onShareClick()
            is TorrentAction.TorrentFileClick -> onTorrentFileClick()
        }
    }

    private fun onAuthorClick(author: Author) = intent {
        postSideEffect(TorrentSideEffect.OpenSearch(Filter(author = author)))
    }

    private fun onBackClick() = intent { postSideEffect(TorrentSideEffect.Back) }

    private fun onCategoryClick(category: Category) = intent {
        postSideEffect(TorrentSideEffect.OpenCategory(category))
    }

    private fun onCommentsClick() = intent {
        postSideEffect(TorrentSideEffect.OpenComments(state.torrent.topic))
    }

    private fun onFavoriteClick() = intent { updateFavoriteUseCase(state.torrent) }

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
                    enrichTopicUseCase(torrent).collectLatest { topicModel ->
                        reduce { state.copy(torrent = topicModel, isLoading = false) }
                    }
                }
                .onFailure { error ->
                    reduce { state.copy(isLoading = false, error = error) }
                }
        }
    }
}
