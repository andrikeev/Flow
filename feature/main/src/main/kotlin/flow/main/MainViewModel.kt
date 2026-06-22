package flow.main

import androidx.lifecycle.ViewModel
import flow.domain.usecase.ObserveSettingsUseCase
import kotlinx.coroutines.flow.map

class MainViewModel(
    observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {
    val theme = observeSettingsUseCase().map { it.theme }
}
