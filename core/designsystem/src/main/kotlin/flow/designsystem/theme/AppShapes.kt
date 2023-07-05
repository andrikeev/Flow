package flow.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

@Immutable
data class AppShapes(
    val rectangle: Shape = RectangleShape,
    val extraSmall: Shape = ShapeDefaults.ExtraSmall,
    val small: Shape = ShapeDefaults.Small,
    val medium: Shape = ShapeDefaults.Medium,
    val large: Shape = ShapeDefaults.Large,
    val extraLarge: Shape = ShapeDefaults.ExtraLarge,
    val circle: Shape = CircleShape,
)

internal val LocaleShapes = staticCompositionLocalOf { AppShapes() }
