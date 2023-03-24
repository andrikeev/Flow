package flow.topic.download

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.DownloadTorrentUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.models.auth.isAuthorized
import flow.topic.torrent.torrent
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
class DownloadViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val downloadTorrentUseCase: DownloadTorrentUseCase,
) : ViewModel(), ContainerHost<DownloadState, DownloadSideEffect> {
    private val torrent = savedStateHandle.torrent

    override val container: Container<DownloadState, DownloadSideEffect> = container(
        initialState = DownloadState.Initial,
    )

    fun perform(action: DownloadAction) = intent {
        when (action) {
            DownloadAction.Dismiss -> postSideEffect(DownloadSideEffect.Dismiss)
            DownloadAction.Download -> startDownload()
            DownloadAction.LoginClick -> postSideEffect(DownloadSideEffect.OpenLogin)
            DownloadAction.OpenFile -> state.let { downloadState ->
                if (downloadState is DownloadState.Completed) {
                    postSideEffect(DownloadSideEffect.OpenFile(downloadState.uri))
                }
            }

            DownloadAction.SettingsClick -> postSideEffect(DownloadSideEffect.OpenSettings)
        }
    }

    private fun startDownload() {
        viewModelScope.launch {
            observeAuthStateUseCase().collectLatest { authState ->
                intent {
                    if (authState.isAuthorized) {
                        reduce { DownloadState.Loading }
                        val uri = downloadTorrentUseCase(torrent)
                        if (uri != null) {
                            reduce { DownloadState.Completed(uri) }
                        }
                    } else {
                        reduce { DownloadState.Unauthorised }
                    }
                }
            }
        }
    }
}
