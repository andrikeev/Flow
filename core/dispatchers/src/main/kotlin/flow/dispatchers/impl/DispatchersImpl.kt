package flow.dispatchers.impl

import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

internal class DispatchersImpl @Inject constructor() : Dispatchers {
    override val default: CoroutineDispatcher
        get() = kotlinx.coroutines.Dispatchers.Default
    override val main: CoroutineDispatcher
        get() = kotlinx.coroutines.Dispatchers.Main
    override val io: CoroutineDispatcher
        get() = kotlinx.coroutines.Dispatchers.IO
}
