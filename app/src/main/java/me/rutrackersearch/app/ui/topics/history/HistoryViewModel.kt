package me.rutrackersearch.app.ui.topics.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.domain.usecase.EnrichTopicsUseCase
import me.rutrackersearch.domain.usecase.ObserveHistoryUseCase
import me.rutrackersearch.domain.usecase.UpdateFavoriteUseCase
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    observeHistoryUseCase: ObserveHistoryUseCase,
    private val enrichTopicsUseCase: EnrichTopicsUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    val state: StateFlow<HistoryState> = observeHistoryUseCase()
        .flatMapLatest { topics -> enrichTopicsUseCase(topics) }
        .map { items ->
            if (items.isEmpty()) {
                HistoryState.Empty
            } else {
                HistoryState.HistoryList(items)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, HistoryState.Initial)

    fun onFavoriteClick(topicModel: TopicModel<out Topic>) {
        viewModelScope.launch {
            updateFavoriteUseCase(topicModel)
        }
    }
}
