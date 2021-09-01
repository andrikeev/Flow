package me.rutrackersearch.app.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.domain.entity.settings.Settings
import me.rutrackersearch.domain.usecase.ObserveSettingsUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {
    val settings: Flow<Settings> = observeSettingsUseCase()
}
