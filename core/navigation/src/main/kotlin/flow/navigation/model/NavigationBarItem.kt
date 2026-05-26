package flow.navigation.model

import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import flow.designsystem.drawables.Icon

data class NavigationBarItem(
    val route: NavKey,
    @StringRes val labelResId: Int,
    val icon: Icon,
)
