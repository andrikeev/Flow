package flow.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.newCancelableScope() = CoroutineScope(coroutineContext + SupervisorJob())

fun CoroutineScope.relaunch(block: suspend CoroutineScope.() -> Unit) {
    coroutineContext.cancelChildren()
    launch(block = block)
}

fun CoroutineScope.launchCatching(
    onFailure: suspend (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit,
): Job = launch { runCatching { coroutineScope(block) }.onFailure { onFailure(it) } }
