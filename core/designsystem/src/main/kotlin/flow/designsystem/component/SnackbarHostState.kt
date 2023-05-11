package flow.designsystem.component

import androidx.compose.runtime.staticCompositionLocalOf

interface SnackbarHostState {
    suspend fun clear()

    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
    )

    companion object {
        object Stub: SnackbarHostState {
            override suspend fun clear() = Unit
            override suspend fun showSnackbar(message: String, actionLabel: String?) = Unit
        }
    }
}

internal class DelegateSnackbarHostState(
    private val realSnackbarHostState: androidx.compose.material3.SnackbarHostState
) : SnackbarHostState {
    override suspend fun clear() {
        realSnackbarHostState.currentSnackbarData?.dismiss()
    }

    override suspend fun showSnackbar(message: String, actionLabel: String?) {
        realSnackbarHostState.showSnackbar(message, actionLabel)
    }
}

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("LocalSnackbarHostState not present")
}
