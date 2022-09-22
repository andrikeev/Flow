package me.rutrackersearch.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

fun CoroutineScope.relaunch(block: suspend CoroutineScope.() -> Unit) {
    coroutineContext.cancelChildren()
    launch(block = block)
}

fun ViewModel.newCancelableScope() = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
