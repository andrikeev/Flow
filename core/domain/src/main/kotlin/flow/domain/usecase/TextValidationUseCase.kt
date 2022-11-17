package flow.domain.usecase

import flow.models.InputState
import javax.inject.Inject

class TextValidationUseCase @Inject constructor() {
    operator fun invoke(text: String): InputState {
        return if (text.isNotBlank()) {
            InputState.Valid(text)
        } else {
            InputState.Empty
        }
    }
}
