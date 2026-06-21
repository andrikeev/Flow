package flow.models

sealed interface LoadState {
    data object Loading : LoadState
    data object NotLoading : LoadState
    data class Error(val error: Throwable) : LoadState
}
