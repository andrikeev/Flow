package flow.models

sealed interface State<out Data> {
    object Loading : State<Nothing>
    data class Success<T>(val data: T) : State<T>
    object Empty : State<Nothing>
    data class Error(val exception: Throwable? = null) : State<Nothing>
}
