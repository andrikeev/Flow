package flow.designsystem.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

@Composable
fun RunOnFirstComposition(
    block: suspend CoroutineScope.() -> Unit,
) = LaunchedEffect(Unit) {
    coroutineContext.job.invokeOnCompletion { error ->
        if (error == null) {
            launch(block = block)
        }
    }
}
