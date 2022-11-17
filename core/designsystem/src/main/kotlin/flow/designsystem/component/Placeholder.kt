package flow.designsystem.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import flow.designsystem.R

@Composable
fun Placeholder(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int,
    @DrawableRes imageRes: Int? = null,
    action: @Composable (() -> Unit)? = null,
) = Placeholder(
    modifier = modifier.fillMaxSize(),
    title = {
        Text(
            text = stringResource(titleRes),
            textAlign = TextAlign.Center,
        )
    },
    subtitle = {
        Text(
            text = stringResource(subtitleRes),
            textAlign = TextAlign.Center,
        )
    },
    image = if (imageRes != null) {
        { Illustration(imageRes) }
    } else {
        null
    },
    action = action,
)

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun Error(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int,
    @DrawableRes imageRes: Int,
    onRetryClick: (() -> Unit)? = null,
) = Placeholder(
    modifier = modifier.fillMaxSize(),
    title = {
        Text(
            text = stringResource(titleRes),
            textAlign = TextAlign.Center,
        )
    },
    subtitle = {
        Text(
            text = stringResource(subtitleRes),
            textAlign = TextAlign.Center,
        )
    },
    image = { Illustration(imageRes) },
    action = if (onRetryClick != null) {
        {
            Button(
                onClick = onRetryClick,
                text = stringResource(R.string.designsystem_action_retry),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    } else {
        null
    }
)

@Composable
fun Empty(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int,
    @DrawableRes imageRes: Int,
) = Placeholder(
    modifier = modifier.fillMaxSize(),
    title = {
        Text(
            text = stringResource(titleRes),
            textAlign = TextAlign.Center,
        )
    },
    subtitle = {
        Text(
            text = stringResource(subtitleRes),
            textAlign = TextAlign.Center,
        )
    },
    image = { Illustration(imageRes) },
)

@Composable
internal fun Placeholder(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    image: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
) {
    Box(modifier = modifier.padding(32.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            title?.run {
                ProvideTextStyle(MaterialTheme.typography.headlineSmall, title)
            }
            subtitle?.run {
                Box(modifier = Modifier.padding(top = 8.dp)) {
                    ProvideTextStyle(
                        value = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.outline,
                        ),
                        content = subtitle,
                    )
                }
            }
            image?.run {
                BoxWithConstraints(contentAlignment = Alignment.Center) {
                    val availableSize = minOf(maxHeight, maxWidth)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = availableSize >= 272.dp,
                        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom),
                    ) {
                        Box(modifier = Modifier.padding(top = 16.dp)) {
                            image()
                        }
                    }
                }
            }
            action?.run {
                Box(modifier = Modifier.padding(top = 32.dp)) {
                    action()
                }
            }
        }
    }
}

@Composable
internal fun Illustration(
    @DrawableRes resId: Int,
) {
    Image(
        modifier = Modifier.size(160.dp),
        painter = painterResource(resId),
        contentDescription = null,
        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
    )
}
