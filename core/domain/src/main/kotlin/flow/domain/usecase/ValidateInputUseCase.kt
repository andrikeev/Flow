package flow.domain.usecase


class ValidateInputUseCase() {
    operator fun invoke(text: String): Boolean {
        return text.isNotBlank()
    }
}
