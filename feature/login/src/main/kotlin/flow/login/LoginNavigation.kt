package flow.login

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import flow.navigation.viewModel
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute : NavKey

fun EntryProviderScope<NavKey>.addLogin(
    back: () -> Unit,
) {
    entry<LoginRoute> {
        LoginScreen(
            viewModel = viewModel(),
            back = back,
        )
    }
}
