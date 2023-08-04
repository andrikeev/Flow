package flow.common

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@Suppress("FunctionName")
fun <T : Any> SingleItemMutableSharedFlow() =
    MutableSharedFlow<T>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
