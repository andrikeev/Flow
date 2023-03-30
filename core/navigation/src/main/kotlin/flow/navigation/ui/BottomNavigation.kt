package flow.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import flow.designsystem.component.NavigationBar
import flow.designsystem.component.NavigationBarItem
import flow.navigation.model.NavigationBarItem

@Composable
internal fun BottomNavigation(
    items: List<NavigationBarItem>,
    selected: String?,
    onClick: (String) -> Unit,
) = BottomNavigation(
    items = items,
    selected = { it == selected },
    onClick = onClick,
)

@Composable
private fun BottomNavigation(
    items: List<NavigationBarItem>,
    selected: (route: String) -> Boolean,
    onClick: (route: String) -> Unit,
) = NavigationBar {
    items.forEach { tab ->
        NavigationBarItem(
            icon = tab.icon,
            label = stringResource(tab.labelResId),
            selected = selected(tab.route),
            onClick = { onClick(tab.route) },
        )
    }
}
