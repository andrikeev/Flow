package me.rutrackersearch.app.ui.topic.torrent

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.args.requireTorrent
import me.rutrackersearch.app.ui.common.Result
import me.rutrackersearch.app.ui.common.Result.Content
import me.rutrackersearch.app.ui.common.Result.Error
import me.rutrackersearch.app.ui.common.Result.Loading
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.FavoriteClick
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.RetryClick
import me.rutrackersearch.app.ui.topic.torrent.TorrentAction.TorrentFileClick
import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.Torrent
import me.rutrackersearch.domain.entity.user.AuthState
import me.rutrackersearch.domain.usecase.DownloadTorrentUseCase
import me.rutrackersearch.domain.usecase.EnrichTopicUseCase
import me.rutrackersearch.domain.usecase.EnrichTorrentUseCase
import me.rutrackersearch.domain.usecase.ObserveAuthStateUseCase
import me.rutrackersearch.domain.usecase.UpdateFavoriteUseCase
import me.rutrackersearch.domain.usecase.VisitTopicUseCase
import javax.inject.Inject

@HiltViewModel
class TorrentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeAuthStateUseCase: ObserveAuthStateUseCase,
    enrichTopicUseCase: EnrichTopicUseCase,
    private val enrichTorrentUseCase: EnrichTorrentUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val visitTopicUseCase: VisitTopicUseCase,
    private val downloadTorrentUseCase: DownloadTorrentUseCase,
) : ViewModel() {
    private val torrent = savedStateHandle.requireTorrent()
    private val mutableTorrent = MutableStateFlow(torrent)
    private val mutableResult = MutableStateFlow<Result<Torrent>>(Loading())
    private val mutableDownloadState = MutableStateFlow<Uri?>(null)

    val authState: StateFlow<AuthState> = observeAuthStateUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, AuthState.Unauthorized)

    val state: StateFlow<TorrentState> = combine(
        mutableTorrent.flatMapLatest { enrichTopicUseCase(it) },
        mutableResult,
        mutableDownloadState,
    ) { data, state, uri ->
        when (state) {
            is Loading -> TorrentState.Loading(data, uri)
            is Content -> TorrentState.Loaded(data, uri)
            is Error -> TorrentState.Error(data, uri, state.error)
        }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            TorrentState.Loading(TopicModel(torrent), null)
        )

    init {
        viewModelScope.launch {
            loadTorrent()
        }
        viewModelScope.launch { visitTopicUseCase(torrent) }
    }

    fun perform(action: TorrentAction) {
        when (action) {
            is FavoriteClick -> viewModelScope.launch {
                updateFavoriteUseCase(action.torrent)
            }
            RetryClick -> viewModelScope.launch {
                loadTorrent()
            }
            TorrentFileClick -> viewModelScope.launch {
                downloadTorrentFile()
            }
            else -> Unit
        }
    }

    private suspend fun loadTorrent() {
        mutableResult.emit(Loading())
        kotlin.runCatching { enrichTorrentUseCase(mutableTorrent.value) }
            .onSuccess { torrent ->
                mutableTorrent.emit(torrent)
                mutableResult.emit(Content(torrent))
            }
            .onFailure { mutableResult.emit(Error(it)) }
    }

    private suspend fun downloadTorrentFile() {
        val uri = downloadTorrentUseCase(torrent)
        if (uri != null) {
            mutableDownloadState.emit(Uri.parse(uri))
        }
    }
}
