package me.rutrackersearch.app.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.platform.LocalOpenLinkHandler
import me.rutrackersearch.app.ui.theme.borders
import me.rutrackersearch.models.topic.Content
import me.rutrackersearch.models.topic.PostContent
import me.rutrackersearch.models.topic.TextContent

@Composable
fun Post(
    modifier: Modifier = Modifier,
    content: Content,
) {
    ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
        FlowRow(modifier = modifier) { Content(content) }
    }
}

@Composable
private fun FlowContent(
    modifier: Modifier = Modifier,
    content: List<Content>
) {
    FlowRow(modifier = modifier) { Content(content) }
}

@Composable
private fun Content(content: List<Content>) {
    if (content.isTextContent()) {
        TextContent(content.filterIsInstance<TextContent>())
    } else {
        content.forEach { Content(it) }
    }
}

@Composable
private fun Content(content: Content) {
    when (content) {
        is TextContent -> TextContent(content)
        is PostContent -> PostContent(content)
    }
}

@Composable
private fun TextContent(textContent: List<TextContent>) {
    Text(text = buildAnnotatedString { append(textContent) })
    Divider(color = Color.Transparent)
}

@Composable
private fun TextContent(textContent: TextContent) {
    Text(text = buildAnnotatedString { append(textContent) })
    Divider(color = Color.Transparent)
}

private fun AnnotatedString.Builder.append(textContent: TextContent) {
    when (textContent) {
        is TextContent.Text -> append(textContent.text)
        is TextContent.Default -> append(textContent.children)
        is TextContent.StyledText.Bold -> append(buildAnnotatedString {
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append(textContent.children)
        })
        is TextContent.StyledText.Crossed -> append(buildAnnotatedString {
            pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
            append(textContent.children)
        })
        is TextContent.StyledText.Italic -> append(buildAnnotatedString {
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append(textContent.children)
        })
        is TextContent.StyledText.Underscore -> append(buildAnnotatedString {
            pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            append(textContent.children)
        })
    }
}

private fun AnnotatedString.Builder.append(textContent: List<TextContent>) {
    textContent.forEach(this::append)
}

@Composable
private fun PostContent(content: PostContent) {
    when (content) {
        is PostContent.Default -> Content(content.children)
        is PostContent.Box -> Box(content.children)
        is PostContent.Code -> Code(content.children)
        is PostContent.Quote -> Quote(content.title, content.children)
        is PostContent.Spoiler -> Spoiler(content.title, content.children)
        is PostContent.PostList -> Box(content.children)
        is PostContent.Image -> Image(content.src)
        is PostContent.Link -> LinkItem(content.src, content.children)
        PostContent.Hr -> Divider(modifier = Modifier.padding(vertical = 4.dp))
        is PostContent.TorrentMainImage -> Unit
    }
}

@Composable
private fun Box(items: List<Content>) {
    Box(
        modifier = Modifier.border(
            border = MaterialTheme.borders.thin,
            shape = MaterialTheme.shapes.small
        ),
    ) { FlowContent(modifier = Modifier.padding(8.dp), items) }
}

@Composable
private fun Spoiler(
    title: String,
    items: List<Content>,
) {
    Focusable(
        modifier = Modifier.padding(vertical = 8.dp),
        spec = focusableSpec(
            scale = ContentScale.small,
            elevation = ContentElevation.small,
            shape = MaterialTheme.shapes.small,
        ),
    ) {
        var isExpanded by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .border(
                    border = MaterialTheme.borders.thin,
                    shape = MaterialTheme.shapes.small,
                )
                .clip(MaterialTheme.shapes.medium),
        ) {
            Row(
                modifier = Modifier
                    .clickable { isExpanded = !isExpanded }
                    .clip(MaterialTheme.shapes.medium)
                    .padding(8.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                )
                val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
                Icon(
                    modifier = Modifier.rotate(rotation),
                    imageVector = Icons.Outlined.ExpandMore,
                    contentDescription = null,
                )
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
            ) {
                FlowContent(modifier = Modifier.padding(8.dp), items)
            }
        }

    }
}

@Composable
private fun Code(items: List<Content>) {
    ProvideTextStyle(
        value = MaterialTheme.typography.bodySmall.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.Monospace,
        )
    ) { Content(items) }
}

@Composable
private fun Quote(title: String, items: List<Content>) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        ProvideTextStyle(MaterialTheme.typography.labelMedium) {
            Text(text = title)
        }
        ProvideTextStyle(MaterialTheme.typography.bodySmall) {
            Box(items)
        }
    }
}

@Composable
private fun Image(src: String) {
    SubcomposeAsyncImage(
        model = src,
        contentDescription = null,
    ) {
        when (painter.state) {
            AsyncImagePainter.State.Empty,
            is AsyncImagePainter.State.Success -> Image(
                painter = painter,
                contentDescription = null,
            )
            is AsyncImagePainter.State.Loading -> CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                strokeWidth = 2.dp,
            )
            is AsyncImagePainter.State.Error -> Icon(
                modifier = Modifier.size(48.dp),
                imageVector = Icons.Outlined.ImageNotSupported,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
private fun LinkItem(src: String, children: List<Content>) {
    val openLinkHandler = LocalOpenLinkHandler.current
    val snackbarState = LocalSnackbarHostState.current
    val coroutinesScope = rememberCoroutineScope()
    val linkErrorMessage = stringResource(R.string.error_open_url)
    Focusable(
        spec = focusableSpec(
            scale = ContentScale.small,
            elevation = ContentElevation.small,
            shape = MaterialTheme.shapes.extraSmall,
        ),
    ) {
        if (children.isTextContent()) {
            ClickableText(
                onClick = {
                    try {
                        openLinkHandler.openLink(src)
                    } catch (e: Throwable) {
                        coroutinesScope.launch { snackbarState.showSnackbar(linkErrorMessage) }
                    }
                },
                text = buildAnnotatedString { append(children.filterIsInstance<TextContent>()) },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                ),
            )
        } else {
            Box(
                modifier = Modifier
                    .clickable {
                        try {
                            openLinkHandler.openLink(src)
                        } catch (e: Throwable) {
                            coroutinesScope.launch { snackbarState.showSnackbar(linkErrorMessage) }
                        }
                    }
                    .clip(MaterialTheme.shapes.medium),
            ) { Content(children) }
        }
    }
}

private fun List<Content>.isTextContent(): Boolean = all { it is TextContent }