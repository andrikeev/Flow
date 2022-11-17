package flow.models

sealed interface InputState {
    val value: String
        get() = ""

    data class Invalid(override val value: String) : InputState
    data class Valid(override val value: String) : InputState
    object Empty : InputState
    object Initial : InputState

    fun isError() = this is Empty || this is Invalid

    fun isValid() = this is Valid
}
