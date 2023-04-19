package flow.models

sealed interface LoadState {
    object Loading : LoadState
    object NotLoading : LoadState
    data class Error(val error: Throwable) : LoadState
}
