package me.rutrackersearch.app.ui.auth

sealed class InputState(val value: String = "") {
    object Initial : InputState()
    class Valid(value: String) : InputState(value)
    object Empty : InputState()
    class Invalid(value: String) : InputState(value)

    fun isError() = this is Empty || this is Invalid

    fun isValid() = this is Initial || this is Valid
}
