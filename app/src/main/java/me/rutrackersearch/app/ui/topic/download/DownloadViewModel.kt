package me.rutrackersearch.app.ui.topic.download

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.args.requireTorrent
import me.rutrackersearch.domain.usecase.DownloadTorrentUseCase
import me.rutrackersearch.domain.usecase.ObserveAuthStateUseCase
import me.rutrackersearch.models.user.AuthState
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
                    when (authState) {
                        is AuthState.Authorized -> {
                            reduce { DownloadState.Loading }
                            val uri = downloadTorrentUseCase(torrent)
                            if (uri != null) {
                                reduce { DownloadState.Completed(uri) }
                            }
                        }

                        is AuthState.Unauthorized -> reduce { DownloadState.Unauthorised }
                    }
                }
            }
        }
    }
}
