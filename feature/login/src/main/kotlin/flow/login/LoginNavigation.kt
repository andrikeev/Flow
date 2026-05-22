package flow.login

import flow.navigation.NavigationController
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.model.buildRoute
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel

private const val LoginRoute = "login"

context(graphBuilder: NavigationGraphBuilder)
fun addLogin(
    back: () -> Unit,
    animations: NavigationAnimations,
) = with(graphBuilder) {
    addDestination(
        route = buildRoute(LoginRoute),
        arguments = emptyList(),
        animations = animations,
    ) {
        LoginScreen(
            viewModel = viewModel(),
            back = back,
        )
    }
}

context(_: NavigationGraphBuilder, navigationController: NavigationController)
fun openLogin() = with(navigationController) {
    navigate(buildRoute(LoginRoute))
}
