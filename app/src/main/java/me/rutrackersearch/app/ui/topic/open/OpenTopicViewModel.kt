package me.rutrackersearch.app.ui.topic.open

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.args.requireId
import me.rutrackersearch.app.ui.args.requirePid
import me.rutrackersearch.domain.usecase.LoadTopicUseCase
import me.rutrackersearch.models.topic.BaseTopic
import me.rutrackersearch.models.topic.Torrent
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class OpenTopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadTopicUseCase: LoadTopicUseCase,
) : ViewModel(), ContainerHost<OpenTopicState, OpenTopicSideEffect> {
    private val id: String = savedStateHandle.requireId()
    private val pid: String = savedStateHandle.requirePid()

    override val container: Container<OpenTopicState, OpenTopicSideEffect> = container(
        initialState = OpenTopicState.Loading,
        onCreate = { loadTopic() },
    )

    fun perform(action: OpenTopicAction) {
        when (action) {
            OpenTopicAction.BackClick -> onBackClick()
            OpenTopicAction.RetryClick -> loadTopic()
        }
    }

    private fun onBackClick() = intent {
        postSideEffect(OpenTopicSideEffect.Back)
    }

    private fun loadTopic() = intent {
        reduce { OpenTopicState.Loading }
        viewModelScope.launch {
            runCatching { loadTopicUseCase(id, pid) }
                .onSuccess { topic ->
                    when (topic) {
                        is BaseTopic -> postSideEffect(OpenTopicSideEffect.OpenTopic(topic))
                        is Torrent -> postSideEffect(OpenTopicSideEffect.OpenTorrent(topic))
                    }
                }
                .onFailure { reduce { OpenTopicState.Error(it) } }
        }
    }
}
