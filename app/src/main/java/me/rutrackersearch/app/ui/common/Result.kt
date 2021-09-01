package me.rutrackersearch.app.ui.common

import me.rutrackersearch.app.ui.paging.LoadState

sealed interface Result<T> {
    class Loading<T> : Result<T>
    data class Content<T>(val content: T) : Result<T>
    data class Error<T>(val error: Throwable) : Result<T>
}

sealed interface PageResult<T> {
    class Loading<T> : PageResult<T>

    data class Content<T>(
        val content: T,
        val append: LoadState = LoadState.NotLoading,
        val prepend: LoadState = LoadState.NotLoading,
    ) : PageResult<T>

    class Empty<T> : PageResult<T>

    data class Error<T>(val error: Throwable) : PageResult<T>
}
