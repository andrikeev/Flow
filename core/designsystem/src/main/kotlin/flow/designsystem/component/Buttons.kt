package flow.designsystem.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import flow.designsystem.R
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.drawables.Icon
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.designsystem.theme.contentColorFor

@Composable
@NonRestartableComposable
fun Button(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = AppTheme.colors.primary,
) = Button(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    color = color,
) {
    Text(
        text = text,
        overflow = TextOverflow.Clip,
        softWrap = false,
    )
}

@Composable
@NonRestartableComposable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = AppTheme.colors.primary,
    content: @Composable RowScope.() -> Unit,
) = androidx.compose.material3.Button(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    shape = AppTheme.shapes.circle,
    colors = ButtonDefaults.buttonColors(
        containerColor = color,
        contentColor = AppTheme.colors.contentColorFor(color),
        disabledContainerColor = AppTheme.colors.onSurface.copy(alpha = 0.12f),
        disabledContentColor = AppTheme.colors.onSurface.copy(alpha = 0.37f),
    ),
    contentPadding = ButtonDefaults.ContentPadding,
    content = content,
)

@Composable
@NonRestartableComposable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = AppTheme.colors.primary,
) = TextButton(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    contentColor = color,
) {
    Text(
        text = text,
        overflow = TextOverflow.Clip,
        softWrap = false,
    )
}

@Composable
@NonRestartableComposable
private fun TextButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = AppTheme.shapes.medium,
    contentColor: Color = AppTheme.colors.primary,
    content: @Composable RowScope.() -> Unit,
) = androidx.compose.material3.TextButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    border = null,
    colors = ButtonDefaults.textButtonColors(
        containerColor = Color.Transparent,
        contentColor = contentColor,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = AppTheme.colors.onSurface.copy(alpha = 0.37f),
    ),
    contentPadding = ButtonDefaults.ContentPadding,
    content = content,
)

@Composable
@NonRestartableComposable
fun IconButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) = androidx.compose.material3.IconButton(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    interactionSource = interactionSource,
    content = content,
)

@Composable
@NonRestartableComposable
fun IconButton(
    icon: Icon,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified,
    onClick: () -> Unit,
) = IconButton(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    content = {
        Icon(
            icon = icon,
            tint = if (tint == Color.Unspecified) LocalContentColor.current else tint,
            contentDescription = contentDescription,
        )
    },
)

@Composable
fun FavoriteButton(
    favorite: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = IconButton(
    modifier = modifier,
    onClick = onClick,
    content = {
        Crossfade(
            targetState = favorite,
            label = "FavoriteButton_Crossfade",
        ) { favorite ->
            if (favorite) {
                Icon(
                    icon = FlowIcons.FavoriteChecked,
                    tint = AppTheme.colors.primary,
                    contentDescription = stringResource(R.string.designsystem_action_favorite),
                )
            } else {
                Icon(
                    icon = FlowIcons.FavoriteUnchecked,
                    contentDescription = stringResource(R.string.designsystem_action_favorite),
                )
            }
        }
    },
)

@Composable
fun BookmarkButton(
    bookmark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = IconButton(
    modifier = modifier,
    onClick = onClick,
    content = {
        Crossfade(
            targetState = bookmark,
            label = "BookmarkButton_Crossfade",
        ) { bookmark ->
            if (bookmark) {
                Icon(
                    icon = FlowIcons.BookmarkChecked,
                    tint = AppTheme.colors.primary,
                    contentDescription = stringResource(R.string.designsystem_action_favorite),
                )
            } else {
                Icon(
                    icon = FlowIcons.BookmarkUnchecked,
                    contentDescription = stringResource(R.string.designsystem_action_favorite),
                )
            }
        }
    },
)

@Composable
@NonRestartableComposable
fun BackButton(onClick: () -> Unit) = IconButton(
    icon = FlowIcons.BackArrow,
    contentDescription = stringResource(R.string.designsystem_action_back),
    onClick = onClick,
)

@Composable
@NonRestartableComposable
fun SearchButton(onClick: () -> Unit) = IconButton(
    icon = FlowIcons.Search,
    contentDescription = stringResource(R.string.designsystem_action_search),
    onClick = onClick,
)

@ThemePreviews
@Composable
private fun ButtonPreview(@PreviewParameter(ButtonParamsProvider::class) enabled: Boolean) {
    FlowTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    modifier = Modifier.padding(AppTheme.spaces.medium),
                    text = "Primary button",
                    enabled = enabled,
                    onClick = {},
                    color = AppTheme.colors.primary,
                )
                Button(
                    modifier = Modifier.padding(AppTheme.spaces.medium),
                    text = "Accent blue button",
                    enabled = enabled,
                    onClick = {},
                    color = AppTheme.colors.accentBlue,
                )
                Button(
                    modifier = Modifier.padding(AppTheme.spaces.medium),
                    text = "Accent green button",
                    enabled = enabled,
                    onClick = {},
                    color = AppTheme.colors.accentGreen,
                )
                Button(
                    modifier = Modifier.padding(AppTheme.spaces.medium),
                    text = "Accent orange button",
                    enabled = enabled,
                    onClick = {},
                    color = AppTheme.colors.accentOrange,
                )
                Button(
                    modifier = Modifier.padding(AppTheme.spaces.medium),
                    text = "Accent red button",
                    enabled = enabled,
                    onClick = {},
                    color = AppTheme.colors.accentRed,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun TextButtonPreview(@PreviewParameter(ButtonParamsProvider::class) enabled: Boolean) {
    FlowTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextButton(
                    text = "Primary button",
                    enabled = enabled,
                    onClick = {},
                )
                TextButton(
                    text = "Accent blue button",
                    enabled = enabled,
                    onClick = {},
                    color = AppTheme.colors.accentBlue,
                )
                TextButton(
                    text = "Accent green button",
                    enabled = enabled,
                    onClick = {},
                    color = AppTheme.colors.accentGreen,
                )
                TextButton(
                    text = "Accent orange button",
                    enabled = enabled,
                    onClick = {},
                    color = AppTheme.colors.accentOrange,
                )
                TextButton(
                    text = "Accent red button",
                    enabled = enabled,
                    onClick = {},
                    color = AppTheme.colors.accentRed,
                )
            }
        }
    }
}

private class ButtonParamsProvider : CollectionPreviewParameterProvider<Boolean>(true, false)
