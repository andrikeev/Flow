package flow.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import flow.designsystem.component.NavigationBar
import flow.designsystem.component.NavigationBarItem
import flow.navigation.model.NavigationBarItem

@Composable
internal fun BottomNavigation(
    items: List<NavigationBarItem>,
    selected: NavKey?,
    onClick: (NavKey) -> Unit,
) = NavigationBar {
    val haptic = LocalHapticFeedback.current
    items.forEach { tab ->
        NavigationBarItem(
            icon = tab.icon,
            label = stringResource(tab.labelResId),
            selected = tab.route == selected,
            onClick = {
                onClick(tab.route)
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            },
        )
    }
}
