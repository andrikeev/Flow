package flow.models

sealed interface State<out Data> {
    data object Loading : State<Nothing>
    data class Success<T>(val data: T) : State<T>
    data object Empty : State<Nothing>
    data class Error(val exception: Throwable? = null) : State<Nothing>
}
