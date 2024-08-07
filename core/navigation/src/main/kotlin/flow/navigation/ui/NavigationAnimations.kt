package flow.navigation.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.Alignment
import androidx.navigation.NavBackStackEntry

typealias EnterAnimation = AnimationScope.() -> EnterTransition
typealias ExitAnimation = AnimationScope.() -> ExitTransition
typealias EnterAnimationConverter = AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
typealias ExitAnimationConverter = AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?

data class NavigationAnimations(
    val enterTransition: EnterAnimation? = null,
    val exitTransition: ExitAnimation? = null,
    val popEnterTransition: EnterAnimation? = enterTransition,
    val popExitTransition: ExitAnimation? = exitTransition,
) {
    companion object {
        val Default = NavigationAnimations()
        val ScaleInOutAnimation = NavigationAnimations(
            enterTransition = {
                scaleIn(initialScale = 0.75f) +
                    expandIn(
                        expandFrom = Alignment.Center,
                        initialSize = { it / 4 },
                    ) +
                    fadeIn()
            },
            exitTransition = null,
            popEnterTransition = null,
            popExitTransition = {
                fadeOut() +
                    shrinkOut(
                        shrinkTowards = Alignment.Center,
                        targetSize = { it / 4 },
                    ) +
                    scaleOut(targetScale = 0.75f)
            },
        )
        val FadeInOutAnimations = NavigationAnimations(
            enterTransition = { fadeIn() },
            exitTransition = null,
            popEnterTransition = null,
            popExitTransition = { fadeOut() },
        )

        fun slideInLeft() = slideInHorizontally { it }
        fun slideOutLeft() = fadeOut() + slideOutHorizontally { it }
        fun slideInRight() = slideInHorizontally { -it }
        fun slideOutRight() = fadeOut() + slideOutHorizontally { -it }
    }
}

data class AnimationScope(
    val from: AnimationDestination,
    val to: AnimationDestination,
)

data class AnimationDestination(
    val graph: String? = null,
    val route: String?,
)

internal fun EnterAnimation?.toEnterTransition(): EnterAnimationConverter? =
    this?.let { animation -> { withAnimationScope(animation::invoke) } }

internal fun ExitAnimation?.toExitTransition(): ExitAnimationConverter? =
    this?.let { animation -> { withAnimationScope(animation::invoke) } }

private inline fun <reified R> AnimatedContentTransitionScope<NavBackStackEntry>.withAnimationScope(
    block: AnimationScope.() -> R,
) = with(
    AnimationScope(
        from = AnimationDestination(
            graph = initialState.destination.parent?.route,
            route = initialState.destination.route,
        ),
        to = AnimationDestination(
            graph = targetState.destination.parent?.route,
            route = targetState.destination.route,
        ),
    ),
    block,
)
