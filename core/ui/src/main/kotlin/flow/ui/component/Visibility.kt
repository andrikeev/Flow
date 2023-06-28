package flow.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
class VisibilityState(initialState: Boolean) {
    var visible: Boolean by mutableStateOf(initialState)
        private set

    fun show() {
        visible = true
    }

    fun hide() {
        visible = false
    }

    fun toggle() {
        visible = !visible
    }
}

@Composable
fun rememberVisibilityState(initialState: Boolean = false) = rememberSaveable(
    inputs = arrayOf(initialState),
    saver = Saver(
        save = { it.visible },
        restore = ::VisibilityState,
    ),
    init = { VisibilityState(initialState) },
)
