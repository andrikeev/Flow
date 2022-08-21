package me.rutrackersearch.app.ui.search.input

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.args.requireFilter
import me.rutrackersearch.app.ui.search.input.SearchInputAction.BackClick
import me.rutrackersearch.app.ui.search.input.SearchInputAction.ClearInputClick
import me.rutrackersearch.app.ui.search.input.SearchInputAction.InputChanged
import me.rutrackersearch.app.ui.search.input.SearchInputAction.SubmitClick
import me.rutrackersearch.app.ui.search.input.SearchInputAction.SuggestClick
import me.rutrackersearch.app.ui.search.input.SearchInputAction.SuggestSelected
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.search.Suggest
import me.rutrackersearch.domain.usecase.AddSuggestUseCase
import me.rutrackersearch.domain.usecase.ObserveSuggestsUseCase
import javax.inject.Inject

@HiltViewModel
class SearchInputViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeSuggestsUseCase: ObserveSuggestsUseCase,
    private val saveSuggestUseCase: AddSuggestUseCase,
) : ViewModel() {

    private val filter: Filter = savedStateHandle.requireFilter()
    private val query: String = filter.query.orEmpty()

    private val mutableSearchInput =
        MutableStateFlow(TextFieldValue(query, selection = TextRange(query.length)))

    private val suggests: Flow<List<Suggest>> = mutableSearchInput
        .flatMapLatest { observeSuggestsUseCase(it.text) }

    val state: StateFlow<SearchInputState> =
        combine(mutableSearchInput, suggests) { searchInput, suggests ->
            SearchInputState(filter, searchInput, suggests)
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            SearchInputState(filter, mutableSearchInput.value, emptyList())
        )

    fun perform(action: SearchInputAction) {
        when (action) {
            ClearInputClick -> viewModelScope.launch {
                mutableSearchInput.emit(TextFieldValue())
            }
            is InputChanged -> viewModelScope.launch {
                mutableSearchInput.emit(action.value.removeNewLines())
            }
            is SuggestSelected -> viewModelScope.launch {
                val text = action.suggest.value
                mutableSearchInput.emit(TextFieldValue(text, TextRange(text.length)))
            }
            is SubmitClick -> viewModelScope.launch {
                if (!action.query.isNullOrBlank()) {
                    saveSuggestUseCase(action.query)
                }
            }
            is SuggestClick -> viewModelScope.launch {
                saveSuggestUseCase(action.suggest.value)
            }
            BackClick -> Unit
        }
    }

    private fun TextFieldValue.removeNewLines(): TextFieldValue {
        return copy(text.replace("\n", " "))
    }
}
