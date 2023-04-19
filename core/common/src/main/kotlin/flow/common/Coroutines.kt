package flow.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

fun CoroutineScope.newCancelableScope() = CoroutineScope(coroutineContext + SupervisorJob())

fun CoroutineScope.relaunch(block: suspend CoroutineScope.() -> Unit) {
    coroutineContext.cancelChildren()
    launch(block = block)
}
