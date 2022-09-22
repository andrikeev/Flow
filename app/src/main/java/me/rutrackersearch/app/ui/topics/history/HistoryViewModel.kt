package me.rutrackersearch.app.ui.topics.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.rutrackersearch.domain.usecase.EnrichTopicsUseCase
import me.rutrackersearch.domain.usecase.ObserveHistoryUseCase
import me.rutrackersearch.domain.usecase.UpdateFavoriteUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val enrichTopicsUseCase: EnrichTopicsUseCase,
    private val observeHistoryUseCase: ObserveHistoryUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel(), ContainerHost<HistoryState, HistorySideEffect> {
    override val container: Container<HistoryState, HistorySideEffect> = container(
        initialState = HistoryState.Initial,
        onCreate = { observeHistory() },
    )

    fun perform(action: HistoryAction) {
        when (action) {
            is HistoryAction.FavoriteClick -> viewModelScope.launch { updateFavoriteUseCase(action.topicModel) }
            is HistoryAction.TopicClick -> intent { postSideEffect(HistorySideEffect.OpenTopic(action.topic)) }
            is HistoryAction.TorrentClick -> intent { postSideEffect(HistorySideEffect.OpenTorrent(action.torrent)) }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            observeHistoryUseCase()
                .flatMapLatest(enrichTopicsUseCase::invoke)
                .map { items ->
                    if (items.isEmpty()) {
                        HistoryState.Empty
                    } else {
                        HistoryState.HistoryList(items)
                    }
                }
                .collectLatest { state -> intent { reduce { state } } }
        }
    }
}
