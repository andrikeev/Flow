package flow.designsystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

interface PopupHostState {
    val visible: Boolean
    val content: @Composable ColumnScope.() -> Unit
    fun show(content: @Composable ColumnScope.() -> Unit)
    fun hide()
}

internal class PopupHostStateImpl : PopupHostState {
    override var visible: Boolean by mutableStateOf(false)
        private set

    override var content: (@Composable ColumnScope.() -> Unit) by mutableStateOf(NoContent)
        private set

    override fun show(content: @Composable ColumnScope.() -> Unit) {
        this.visible = true
        this.content = content
    }

    override fun hide() {
        this.visible = false
        this.content = NoContent
    }

    private companion object {
        private val NoContent: (@Composable ColumnScope.() -> Unit) = {}
    }
}

@Composable
internal fun rememberPopupHostState(): PopupHostState {
    return remember { PopupHostStateImpl() }
}

val LocalPopupHostState = staticCompositionLocalOf<PopupHostState> {
    error("LocalSnackbarHostState not present")
}
