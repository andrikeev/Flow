package flow.navigation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import flow.designsystem.component.Scaffold
import flow.navigation.LocalDeepLinks
import flow.navigation.NavigationState
import flow.navigation.Navigator
import flow.navigation.model.NavigationBarItem

@Composable
fun MobileNavigation(
    navigator: Navigator,
    navigationBarItems: List<NavigationBarItem>,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
) {
    val state = navigator.state
    val entries = state.toEntries(entryProvider = entryProvider)

    Scaffold(
        content = { padding ->
            NavDisplay(
                modifier = Modifier.padding(padding),
                entries = entries,
                onBack = { navigator.popBackStack() },
                sceneStrategies = remember { listOf(DialogSceneStrategy()) },
                transitionSpec = forwardTransitionSpec(),
                popTransitionSpec = backTransitionSpec(),
                predictivePopTransitionSpec = predictiveBackTransitionSpec(),
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = state.isAtTopLevelRoot,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                BottomNavigation(
                    items = navigationBarItems,
                    selected = state.topLevelRoute,
                    onClick = navigator::navigate,
                )
            }
        },
    )

    val deepLinks = LocalDeepLinks.current
    LaunchedEffect(deepLinks.initialDeepLink) {
        deepLinks.initialDeepLink?.let(navigator::deeplink)
    }
    LaunchedEffect(deepLinks.deepLink) {
        deepLinks.deepLink?.let(navigator::deeplink)
    }
}
