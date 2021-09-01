package me.rutrackersearch.app.ui.common

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.rutrackersearch.app.R

@Composable
fun Placeholder(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
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
            icon?.run {
                BoxWithConstraints(contentAlignment = Alignment.Center) {
                    val availableSize = minOf(maxHeight, maxWidth)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = availableSize > 200.dp,
                        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
                    ) {
                        Box(modifier = Modifier.padding(top = 32.dp)) {
                            icon()
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
    error: Throwable,
    onRetryClick: (() -> Unit)? = null,
) = Placeholder(
    modifier = modifier.fillMaxSize(),
    title = { Text(stringResource(R.string.error_title)) },
    subtitle = {
        Text(
            text = stringResource(error.getStringRes()),
            textAlign = TextAlign.Center,
        )
    },
    icon = {
        Image(
            painter = painterResource(error.getIllRes()),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
        )
    },
    action = if (onRetryClick != null) {
        {
            Button(
                onClick = onRetryClick,
                text = stringResource(R.string.action_retry),
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
    @DrawableRes iconRes: Int,
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
    icon = {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
        )
    },
)
