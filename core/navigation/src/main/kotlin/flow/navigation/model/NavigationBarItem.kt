package flow.navigation.model

import androidx.annotation.StringRes
import flow.designsystem.drawables.Icon

data class NavigationBarItem(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: Icon,
)
