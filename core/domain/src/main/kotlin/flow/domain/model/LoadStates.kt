package flow.domain.model

import flow.models.LoadState

data class LoadStates(
    val refresh: LoadState = LoadState.NotLoading,
    val append: LoadState = LoadState.NotLoading,
    val prepend: LoadState = LoadState.NotLoading,
) {
    companion object {
        val Idle = LoadStates()
    }
}
