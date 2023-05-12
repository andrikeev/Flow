package flow.domain.usecase

import javax.inject.Inject

class ValidateInputUseCase @Inject constructor() {
    operator fun invoke(text: String): Boolean {
        return text.isNotBlank()
    }
}
