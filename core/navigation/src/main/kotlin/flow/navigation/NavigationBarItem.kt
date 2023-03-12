package flow.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationBarItem(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: ImageVector,
)
