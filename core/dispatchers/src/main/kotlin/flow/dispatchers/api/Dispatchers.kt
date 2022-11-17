package flow.dispatchers.api

import kotlinx.coroutines.CoroutineDispatcher

interface Dispatchers {
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
}
