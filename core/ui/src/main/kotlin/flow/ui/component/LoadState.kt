package flow.ui.component

sealed interface LoadState {
    object Loading : LoadState
    object NotLoading : LoadState
    data class Error(val error: Throwable) : LoadState
}

data class LoadStates(
    val refresh: LoadState = LoadState.NotLoading,
    val append: LoadState = LoadState.NotLoading,
    val prepend: LoadState = LoadState.NotLoading,
) {
    companion object {
        val idle = LoadStates()
        val refresh = LoadStates(refresh = LoadState.Loading)
        val append = LoadStates(append = LoadState.Loading)
    }
}
