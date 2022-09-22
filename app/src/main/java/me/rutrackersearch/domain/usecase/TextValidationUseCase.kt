package me.rutrackersearch.domain.usecase

import me.rutrackersearch.models.InputState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextValidationUseCase @Inject constructor() {
    operator fun invoke(text: String): InputState {
        return if (text.isNotBlank()) {
            InputState.Valid(text)
        } else {
            InputState.Empty
        }
    }
}
