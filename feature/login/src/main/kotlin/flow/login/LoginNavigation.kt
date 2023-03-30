package flow.login

import flow.navigation.NavigationController
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel

private val NavigationGraphBuilder.LoginRoute
    get() = route("Login")

data class LoginNavigation(
    val addLogin: NavigationGraphBuilder.(
        back: () -> Unit,
        animations: NavigationAnimations,
    ) -> Unit,
    val openLogin: NavigationController.() -> Unit,
)

fun NavigationGraphBuilder.buildLoginNavigation() = LoginNavigation(
    addLogin = NavigationGraphBuilder::addLogin,
    openLogin = { navigate(LoginRoute) }
)

private fun NavigationGraphBuilder.addLogin(
    back: () -> Unit,
    animations: NavigationAnimations,
) = addDestination(
    route = LoginRoute,
    arguments = emptyList(),
    animations = animations,
) {
    LoginScreen(
        viewModel = viewModel(),
        back = back,
    )
}
