package flow.designsystem.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.job

@Composable
fun RunOnFirstComposition(block: () -> Unit) {
    LaunchedEffect(Unit) {
        coroutineContext.job.invokeOnCompletion { error ->
            if (error == null) {
                block()
            }
        }
    }
}
