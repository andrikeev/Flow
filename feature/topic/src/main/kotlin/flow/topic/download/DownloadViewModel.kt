package flow.topic.download

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.DownloadTorrentUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.models.user.isAuthorized
import flow.ui.args.requireTorrent
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
    private val torrent = savedStateHandle.requireTorrent()

    override val container: Container<DownloadState, DownloadSideEffect> = container(
        initialState = DownloadState.Initial,
    )

    fun perform(action: flow.topic.download.DownloadAction) = intent {
        when (action) {
            flow.topic.download.DownloadAction.Dismiss -> postSideEffect(DownloadSideEffect.Dismiss)
            flow.topic.download.DownloadAction.Download -> startDownload()
            flow.topic.download.DownloadAction.LoginClick -> postSideEffect(DownloadSideEffect.OpenLogin)
            flow.topic.download.DownloadAction.OpenFile -> state.let { downloadState ->
                if (downloadState is DownloadState.Completed) {
                    postSideEffect(DownloadSideEffect.OpenFile(downloadState.uri))
                }
            }

            flow.topic.download.DownloadAction.SettingsClick -> postSideEffect(DownloadSideEffect.OpenSettings)
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
