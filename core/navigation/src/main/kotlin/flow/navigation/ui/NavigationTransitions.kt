package flow.navigation.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene

private const val MotionDurationLong = 400
private const val MotionDurationShort = 250

/** Forward push: incoming slides in from the right, outgoing parallax-shifts and fades. */
internal fun <T : Any> forwardTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    slideInHorizontally(
        animationSpec = tween(MotionDurationLong, easing = EaseOutCubic),
        initialOffsetX = { width -> width },
    ) + fadeIn(
        animationSpec = tween(MotionDurationLong, easing = EaseOutCubic),
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(MotionDurationLong, easing = EaseOutCubic),
        targetOffsetX = { width -> -width / 4 },
    ) + fadeOut(
        animationSpec = tween(MotionDurationShort, easing = EaseOutCubic),
    )
}

/** Pop: outgoing slides out to the right, incoming parallax-shifts in and fades. */
internal fun <T : Any> backTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    slideInHorizontally(
        animationSpec = tween(MotionDurationLong, easing = EaseOutCubic),
        initialOffsetX = { width -> -width / 4 },
    ) + fadeIn(
        animationSpec = tween(MotionDurationLong, easing = EaseOutCubic),
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(MotionDurationLong, easing = EaseOutCubic),
        targetOffsetX = { width -> width },
    ) + fadeOut(
        animationSpec = tween(MotionDurationShort, easing = EaseOutCubic),
    )
}

/** Predictive back: same shape as a pop, driven by gesture progress by NavDisplay. */
internal fun <T : Any> predictiveBackTransitionSpec():
    AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform = {
    backTransitionSpec<T>().invoke(this)
}
