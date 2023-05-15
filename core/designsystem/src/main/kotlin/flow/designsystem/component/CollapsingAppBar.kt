package flow.designsystem.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import flow.designsystem.component.CollapsingAppBarDefaults.MaxAppBarHeight
import flow.designsystem.component.CollapsingAppBarDefaults.MinAppBarHeight
import flow.designsystem.component.CollapsingAppBarDefaults.MinHeight
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import kotlin.math.roundToInt

@Composable
fun CollapsingAppBar(
    modifier: Modifier = Modifier,
    isDark: Boolean = true,
    backgroundImage: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    additionalContent: @Composable () -> Unit = {},
    appBarState: CollapsingAppBarState = rememberCollapsingAppBarState(),
) {
    FlowTheme(isDark = isDark) {
        AppBarContainer {
            CollapsingAppBar(
                modifier = modifier,
                backgroundImage = backgroundImage,
                navigationIcon = navigationIcon,
                title = title,
                actions = actions,
                additionalContent = additionalContent,
                appBarState = appBarState,
            )
        }
    }
}

@Composable
internal fun CollapsingAppBar(
    modifier: Modifier = Modifier,
    backgroundImage: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    additionalContent: @Composable () -> Unit = {},
    appBarState: CollapsingAppBarState = rememberCollapsingAppBarState(),
) {
    val minHeight = appBarState.minHeight.value
    val maxHeight = appBarState.maxHeight.value
    val height = appBarState.height.value
    val horizontalPadding = AppTheme.spaces.large
    SubcomposeLayout(modifier = modifier.fillMaxWidth()) { constraints ->
        val horizontalOffset = horizontalPadding.roundToPx()
        val width = constraints.maxWidth
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val collapseFraction = coercedFraction(maxHeight, minHeight, height)

        val navigationIconPlaceable = measure(
            slotId = CollapsingAppBarContent.NavigationIcon,
            constraints = looseConstraints,
        ) {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 4.dp)
                    .height(MinHeight),
                contentAlignment = Alignment.Center,
                content = { navigationIcon() },
            )
        }

        val actionsPlaceable = measure(
            slotId = CollapsingAppBarContent.Actions,
            constraints = looseConstraints,
        ) {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(end = 4.dp)
                    .height(MinHeight),
                verticalAlignment = Alignment.CenterVertically,
                content = actions,
            )
        }

        val additionalContentPlaceable = measure(
            slotId = CollapsingAppBarContent.AdditionalContent,
            constraints = looseConstraints.copy(
                maxWidth = width - 2 * horizontalOffset,
            ),
            content = { Box(content = { additionalContent() }) },
        )

        @Composable
        fun titleContent() {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 4.dp)
                    .heightIn(min = MinHeight)
                    .height(IntrinsicSize.Max)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
            ) {
                ProvideTextStyle(AppTheme.typography.titleMedium) {
                    title()
                }
            }
        }

        val collapsedTitleSize = measure(
            slotId = CollapsingAppBarContent.CollapsedTitleSize,
            constraints = looseConstraints.copy(
                maxWidth = width - 2 * horizontalOffset,
                maxHeight = Int.MAX_VALUE,
            ),
            content = { titleContent() },
        )

        val collapsedShrunkTitleSize = measure(
            slotId = CollapsingAppBarContent.CollapsedShrunkTitleSize,
            constraints = looseConstraints.copy(
                maxWidth = width - maxOf(navigationIconPlaceable.width, horizontalOffset) -
                        maxOf(actionsPlaceable.width, horizontalOffset),
                maxHeight = Int.MAX_VALUE,
            ),
            content = { titleContent() },
        )

        val titleAvailableHeight = height - additionalContentPlaceable.height
        val collapsedTitleAppBarHeight = (minHeight + collapsedTitleSize.height)

        val widthCollapseFraction = coercedFraction(
            max = collapsedTitleAppBarHeight,
            min = collapsedShrunkTitleSize.height + additionalContentPlaceable.height,
            current = height,
        )

        val titlePlaceable = measure(
            slotId = CollapsingAppBarContent.Title,
            constraints = looseConstraints.copy(
                maxWidth = lerp(
                    collapsedTitleSize.width,
                    collapsedShrunkTitleSize.width,
                    widthCollapseFraction,
                ),
                maxHeight = titleAvailableHeight,
            ),
            content = { titleContent() },
        )

        val backgroundImagePlaceable = measure(
            slotId = CollapsingAppBarContent.BackgroundImage,
            constraints = looseConstraints,
        ) {
            val elevation by animateDpAsState(
                targetValue = if (collapseFraction < 1f) {
                    0.dp
                } else {
                    AppTheme.elevations.medium
                },
                label = "CollapsingAppBar_Elevation"
            )
            val color = lerp(
                AppTheme.colors.surface,
                AppTheme.colors.primaryContainer,
                collapseFraction,
            )
            val fade = color.copy(alpha = collapseFraction)
            val gradientHeight = height.toFloat()
            val topGradientStop = maxOf(
                navigationIconPlaceable.height,
                actionsPlaceable.height,
                MinHeight.roundToPx(),
            ) / gradientHeight
            val bottomGradientStop =
                (height - additionalContentPlaceable.height - titlePlaceable.height) / gradientHeight
            val gradient = Brush.verticalGradient(
                0f to color,
                topGradientStop to fade,
                bottomGradientStop to fade,
                1f to color,
                endY = gradientHeight,
            )
            Box(
                modifier = Modifier
                    .heightIn(minHeight.toDp(), height.toDp())
                    .wrapContentSize()
                    .drawWithContent {
                        drawContent()
                        drawRect(gradient)
                    }
                    .shadow(elevation),
                content = { backgroundImage() },
            )
        }

        layout(width, height) {
            backgroundImagePlaceable.place(0, 0, -1f)
            navigationIconPlaceable.place(0, 0)
            actionsPlaceable.place(width - actionsPlaceable.width, 0)
            if (maxHeight < collapsedTitleAppBarHeight) {
                appBarState.updateMaxHeight(collapsedTitleAppBarHeight)
            } else {
                titlePlaceable.place(
                    x = maxOf(
                        horizontalOffset,
                        (navigationIconPlaceable.width * widthCollapseFraction).roundToInt()
                    ),
                    y = (titleAvailableHeight - titlePlaceable.height).coerceAtLeast(0),
                )
            }
            if (additionalContentPlaceable.height > 0) {
                appBarState.updateMinHeight(minHeight + additionalContentPlaceable.height)
            }
            additionalContentPlaceable.place(horizontalOffset, titleAvailableHeight)
        }
    }
}

private fun SubcomposeMeasureScope.measure(
    slotId: Any?,
    constraints: Constraints,
    content: @Composable () -> Unit,
): Placeable = subcompose(slotId, content).first().measure(constraints)

private fun lerp(startValue: Int, endValue: Int, fraction: Float): Int {
    return startValue + (fraction * (endValue - startValue)).roundToInt()
}

private fun coercedFraction(max: Int, min: Int, current: Int): Float {
    return if (max <= min) 1f else ((max - current).toFloat() / (max - min)).coerceIn(+0.0f, 1.0f)
}

private enum class CollapsingAppBarContent {
    BackgroundImage,
    NavigationIcon,
    Actions,
    AdditionalContent,
    CollapsedTitleSize,
    CollapsedShrunkTitleSize,
    Title,
}

private object CollapsingAppBarDefaults {
    val MinHeight = 56.dp

    val MinAppBarHeight: Dp
        @Composable
        get() = MinHeight + WindowInsets.systemBars.asPaddingValues().calculateTopPadding()

    val MaxAppBarHeight: Dp
        @Composable
        get() = LocalConfiguration.current.screenHeightDp.div(2).dp
}

@Stable
class CollapsingAppBarState(
    defaultMinHeight: Int,
    defaultMaxHeight: Int,
) : AppBarState {
    override val elevated: Boolean
        get() = true

    private var isDefaultMinHeight = true
    private var isDefaultMaxHeight = true

    fun updateMinHeight(value: Int) {
        if (isDefaultMinHeight) {
            isDefaultMinHeight = false
            minHeight.value = value
            if (maxHeight.value < value) {
                maxHeight.value = value
                height.value = value
            }
        }
    }

    fun updateMaxHeight(value: Int) {
        if (isDefaultMaxHeight) {
            isDefaultMaxHeight = false
            maxHeight.value = value
            height.value = value
        }
    }

    val minHeight = mutableStateOf(defaultMinHeight)
    val maxHeight = mutableStateOf(defaultMaxHeight)
    val height = mutableStateOf(defaultMaxHeight)
}

@Stable
class CollapsingAppBarBehavior(
    override val appBarState: CollapsingAppBarState,
) : AppBarBehavior {
    private val minHeight get() = appBarState.minHeight.value
    private val maxHeight get() = appBarState.maxHeight.value
    private val height get() = appBarState.height.value

    override val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val offset = available.y
            return if (offset < 0) {
                Offset(
                    x = 0f,
                    y = maxOf(offset, (minHeight - height).toFloat())
                ).also { (_, consumedYOffset) ->
                    appBarState.height.value += consumedYOffset.roundToInt()
                }
            } else {
                super.onPreScroll(available, source)
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            val offset = available.y
            return if (offset > 0) {
                Offset(
                    x = 0f,
                    y = minOf(offset, (maxHeight - height).toFloat())
                ).also { (_, consumedYOffset) ->
                    appBarState.height.value += consumedYOffset.roundToInt()
                }
            } else {
                super.onPostScroll(consumed, available, source)
            }
        }
    }
}

@Composable
fun rememberCollapsingAppBarBehavior(
    minHeight: Dp = MinAppBarHeight,
    maxHeight: Dp = MaxAppBarHeight,
) = CollapsingAppBarBehavior(
    rememberCollapsingAppBarState(
        minHeight = minHeight,
        maxHeight = maxHeight,
    ),
)

@Composable
fun rememberCollapsingAppBarState(
    minHeight: Dp = MinAppBarHeight,
    maxHeight: Dp = MaxAppBarHeight,
) = CollapsingAppBarState(
    defaultMinHeight = with(LocalDensity.current) { minHeight.roundToPx() },
    defaultMaxHeight = with(LocalDensity.current) { maxHeight.roundToPx() },
)
