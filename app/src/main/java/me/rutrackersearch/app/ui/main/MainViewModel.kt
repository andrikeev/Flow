package me.rutrackersearch.app.ui.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import me.rutrackersearch.domain.usecase.ObserveSettingsUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {
    val theme = observeSettingsUseCase().map { it.theme }
}
