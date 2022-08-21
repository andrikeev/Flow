package me.rutrackersearch.app.ui.topic.open

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.args.requireId
import me.rutrackersearch.app.ui.args.requirePid
import me.rutrackersearch.app.ui.common.Result
import me.rutrackersearch.domain.usecase.LoadTopicUseCase
import me.rutrackersearch.models.topic.Topic
import javax.inject.Inject

@HiltViewModel
class OpenTopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadTopicUseCase: LoadTopicUseCase,
) : ViewModel() {
    private val id: String = savedStateHandle.requireId()
    private val pid: String = savedStateHandle.requirePid()
    private val mutableState = MutableStateFlow<Result<Topic>>(Result.Loading())

    val state: StateFlow<Result<Topic>> = mutableState

    init {
        loadTopic()
    }

    fun retry() {
        loadTopic()
    }

    private fun loadTopic() {
        viewModelScope.launch {
            mutableState.emit(Result.Loading())
            kotlin.runCatching { loadTopicUseCase(id, pid) }
                .onSuccess { topic ->
                    mutableState.emit(Result.Content(topic))
                }
                .onFailure { mutableState.emit(Result.Error(it)) }
        }
    }
}
