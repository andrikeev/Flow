package me.rutrackersearch.app.ui.search.input

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import me.rutrackersearch.app.ui.args.requireFilter
import me.rutrackersearch.domain.usecase.AddSuggestUseCase
import me.rutrackersearch.domain.usecase.ObserveSuggestsUseCase
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.search.Suggest
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
class SearchInputViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeSuggestsUseCase: ObserveSuggestsUseCase,
    private val saveSuggestUseCase: AddSuggestUseCase,
) : ViewModel(), ContainerHost<SearchInputState, SearchInputSideEffect> {
    private val filter: Filter = savedStateHandle.requireFilter()
    private val observeSuggestsScope = newCancelableScope()

    override val container: Container<SearchInputState, SearchInputSideEffect> = container(
        initialState = SearchInputState(),
        onCreate = { onInputChanged(filter.query.toTextFieldValue()) },
    )

    fun perform(action: SearchInputAction) {
        when (action) {
            is SearchInputAction.BackClick -> onBackClick()
            is SearchInputAction.ClearInputClick -> onInputChanged(TextFieldValue())
            is SearchInputAction.InputChanged -> onInputChanged(action.value.removeNewLines())
            is SearchInputAction.SubmitClick -> onSubmit(action.query)
            is SearchInputAction.SuggestClick -> onSubmit(action.suggest.value)
            is SearchInputAction.SuggestSelected -> onSuggestSelected(action.suggest)
        }
    }

    private fun onBackClick() = intent {
        postSideEffect(SearchInputSideEffect.Back)
    }

    private fun onInputChanged(value: TextFieldValue) = intent {
        reduce { state.copy(searchInput = value) }
        observeSuggestsScope.relaunch {
            observeSuggestsUseCase(value.text).collectLatest { suggests ->
                reduce { state.copy(suggests = suggests) }
            }
        }
    }

    private fun onSubmit(query: String?) = intent {
        saveSuggestUseCase(query)
        postSideEffect(SearchInputSideEffect.OpenSearch(filter.copy(query = query)))
    }

    private fun onSuggestSelected(suggest: Suggest) = intent {
        reduce { state.copy(searchInput = suggest.value.toTextFieldValue()) }
    }

    private fun TextFieldValue.removeNewLines(): TextFieldValue = copy(text.replace("\n", " "))

    private fun String?.toTextFieldValue(): TextFieldValue =
        this.orEmpty().let { TextFieldValue(it, TextRange(it.length)) }
}
