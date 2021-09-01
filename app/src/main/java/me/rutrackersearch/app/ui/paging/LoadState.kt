package me.rutrackersearch.app.ui.paging

sealed interface LoadState {
    object Loading : LoadState
    object NotLoading : LoadState
    data class Error(val error: Throwable) : LoadState
}

data class LoadStates(
    val refresh: LoadState = LoadState.NotLoading,
    val prepend: LoadState = LoadState.NotLoading,
    val append: LoadState = LoadState.NotLoading,
) {
    companion object {
        val initial = LoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading,
            append = LoadState.NotLoading,
        )
    }
}
