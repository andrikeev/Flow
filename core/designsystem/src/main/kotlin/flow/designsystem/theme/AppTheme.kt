package flow.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object AppTheme {
    val borders: AppBorders
        @Composable
        @ReadOnlyComposable
        get() = LocalBorders.current

    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val elevations: AppElevations
        @Composable
        @ReadOnlyComposable
        get() = LocalElevations.current

    val shapes: AppShapes
        @Composable
        @ReadOnlyComposable
        get() = LocaleShapes.current

    val sizes: AppSizes
        @Composable
        @ReadOnlyComposable
        get() = LocaleSizes.current

    val spaces: AppSpaces
        @Composable
        @ReadOnlyComposable
        get() = LocaleSpaces.current

    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}
