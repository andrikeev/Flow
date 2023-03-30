package flow.navigation.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry

typealias EnterAnimation = (from: String?) -> EnterTransition
typealias ExitAnimation = (from: String?) -> ExitTransition

data class NavigationAnimations(
    val enterTransition: EnterAnimation? = null,
    val exitTransition: ExitAnimation? = null,
    val popEnterTransition: EnterAnimation? = enterTransition,
    val popExitTransition: ExitAnimation? = exitTransition,
) {
    companion object {
        val Default = NavigationAnimations()
        val ScaleInOutAnimation = NavigationAnimations(
            enterTransition = { scaleIn(initialScale = 1.5f) + fadeIn() },
            exitTransition = { scaleOut(targetScale = 1.5f) + fadeOut() },
        )
        fun slideInLeft() = slideInHorizontally { it } + fadeIn()
        fun slideOutLeft() = slideOutHorizontally { it } + fadeOut()
        fun slideInRight() = slideInHorizontally { -it } + fadeIn()
        fun slideOutRight() = slideOutHorizontally { -it } + fadeOut()
    }
}

internal fun EnterAnimation?.toEnterTransition(): (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? =
    this?.let { { invoke(initialState.destination.parent?.route ?: initialState.destination.route) } }

internal fun ExitAnimation?.toExitTransition(): (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? =
    this?.let { { invoke(initialState.destination.parent?.route ?: initialState.destination.route) } }
