package flow.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Resolves a [ViewModel] from Koin. Optional [parameters] are forwarded to the Koin
 * definition (for view models that take runtime arguments, e.g. a topic id or a search
 * filter); with no parameters the dependencies are resolved from the Koin graph.
 */
@Composable
inline fun <reified VM : ViewModel> viewModel(vararg parameters: Any?): VM =
    koinViewModel { parametersOf(*parameters) }
