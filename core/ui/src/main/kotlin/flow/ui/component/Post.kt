package flow.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import flow.designsystem.component.CircularProgressIndicator
import flow.designsystem.component.Divider
import flow.designsystem.component.ExpandCollapseIcon
import flow.designsystem.component.Icon
import flow.designsystem.component.LocalSnackbarHostState
import flow.designsystem.component.ProvideTextStyle
import flow.designsystem.component.SnackbarHostState
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.ThemePreviews
import flow.designsystem.component.rememberExpandState
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.models.topic.Content
import flow.models.topic.PostContent
import flow.models.topic.TextContent
import flow.ui.R
import flow.ui.platform.LocalOpenLinkHandler
import flow.ui.platform.OpenLinkHandler
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Post(
    modifier: Modifier = Modifier,
    content: Content,
) {
    ProvideTextStyle(AppTheme.typography.bodyMedium) {
        FlowRow(modifier = modifier) { Content(content) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowContent(
    modifier: Modifier = Modifier,
    content: List<Content>,
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
    Text(
        modifier = Modifier.padding(vertical = AppTheme.spaces.small),
        text = buildAnnotatedString { append(textContent) }
    )
    Divider(color = Color.Transparent)
}

@Composable
private fun TextContent(textContent: TextContent) {
    Text(
        modifier = Modifier.padding(vertical = AppTheme.spaces.small),
        text = buildAnnotatedString { append(textContent) },
    )
    Divider(color = Color.Transparent)
}

private fun AnnotatedString.Builder.append(textContent: TextContent) {
    when (textContent) {
        is TextContent.Text -> {
            val text = textContent.text
            if (text == "\n" || text.isBlank()) append(textContent.text)
            else append("${textContent.text} ")
        }

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
            pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
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
        is PostContent.Hr -> Divider(modifier = Modifier.padding(vertical = AppTheme.spaces.small))
        is PostContent.TorrentMainImage -> Unit
    }
}

@Composable
private fun Box(items: List<Content>) {
    Surface(
        shape = AppTheme.shapes.small,
        tonalElevation = AppTheme.elevations.medium,
        shadowElevation = AppTheme.elevations.small,
    ) { FlowContent(modifier = Modifier.padding(AppTheme.spaces.medium), items) }
}

@Composable
private fun Spoiler(
    title: String,
    items: List<Content>,
) {
    Surface(
        modifier = Modifier.padding(vertical = AppTheme.spaces.small),
        shape = AppTheme.shapes.small,
        tonalElevation = AppTheme.elevations.small,
        shadowElevation = AppTheme.elevations.small,
    ) {
        Column {
            val expandState = rememberExpandState()
            Row(
                modifier = Modifier
                    .clickable(onClick = expandState::toggle)
                    .clip(AppTheme.shapes.small)
                    .padding(AppTheme.spaces.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                )
                ExpandCollapseIcon(expanded = expandState.expanded)
            }
            AnimatedVisibility(
                visible = expandState.expanded,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
            ) {
                FlowContent(modifier = Modifier.padding(AppTheme.spaces.medium), items)
            }
        }
    }
}

@Composable
private fun Code(items: List<Content>) {
    ProvideTextStyle(
        value = AppTheme.typography.bodySmall.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.Monospace,
        )
    ) { Content(items) }
}

@Composable
private fun Quote(title: String, items: List<Content>) {
    Column(modifier = Modifier.padding(vertical = AppTheme.spaces.mediumSmall)) {
        ProvideTextStyle(AppTheme.typography.labelSmall) {
            Text(
                modifier = Modifier.padding(
                    start = AppTheme.spaces.small,
                    bottom = AppTheme.spaces.small,
                ),
                text = title,
            )
        }
        ProvideTextStyle(AppTheme.typography.bodySmall) {
            Box(items)
        }
    }
}

@Composable
private fun Image(src: String) {
    SubcomposeAsyncImage(
        modifier = Modifier.padding(AppTheme.spaces.extraSmall),
        model = src,
        contentDescription = null, //TODO: add contentDescription
    ) {
        when (painter.state) {
            AsyncImagePainter.State.Empty,
            is AsyncImagePainter.State.Success,
            -> Image(
                painter = painter,
                contentDescription = null, //TODO: add contentDescription
            )

            is AsyncImagePainter.State.Loading -> CircularProgressIndicator(
                modifier = Modifier.size(AppTheme.sizes.medium),
            )

            is AsyncImagePainter.State.Error -> Icon(
                modifier = Modifier.size(AppTheme.sizes.default),
                icon = FlowIcons.ImagePlaceholder,
                tint = AppTheme.colors.outline,
                contentDescription = null, //TODO: add contentDescription
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
    if (children.isTextContent()) {
        ClickableText(
            modifier = Modifier.padding(AppTheme.spaces.small),
            onClick = {
                try {
                    openLinkHandler.openLink(src)
                } catch (e: Throwable) {
                    coroutinesScope.launch { snackbarState.showSnackbar(linkErrorMessage) }
                }
            },
            text = buildAnnotatedString { append(children.filterIsInstance<TextContent>()) },
            style = AppTheme.typography.bodyMedium.copy(
                color = AppTheme.colors.primary,
                textDecoration = TextDecoration.Underline,
            ),
        )
    } else {
        Box(
            modifier = Modifier
                .padding(AppTheme.spaces.small)
                .clickable {
                    try {
                        openLinkHandler.openLink(src)
                    } catch (e: Throwable) {
                        coroutinesScope.launch { snackbarState.showSnackbar(linkErrorMessage) }
                    }
                }
                .clip(AppTheme.shapes.medium),
        ) { Content(children) }
    }
}

private fun List<Content>.isTextContent(): Boolean = all { it.isTextContent() }

private fun Content.isTextContent(): Boolean {
    return when (this) {
        is PostContent.Default -> children.isTextContent()
        is TextContent -> true
        else -> false
    }
}

@ThemePreviews
@Composable
private fun ContentPreview() {
    FlowTheme {
        Surface {
            CompositionLocalProvider(
                LocalOpenLinkHandler provides OpenLinkHandler.Companion.Stub,
                LocalSnackbarHostState provides SnackbarHostState.Companion.Stub,
            ) {
                Post(
                    modifier = Modifier.padding(AppTheme.spaces.medium),
                    content = PostContent.Default(
                        listOf(
                            TextContent.Text("Text"),
                            PostContent.Box(
                                listOf(
                                    TextContent.Text("Text in Box"),
                                    PostContent.Hr,
                                    TextContent.StyledText.Bold(listOf(TextContent.Text("Text in Box Bold"))),
                                )
                            ),
                            PostContent.Link("link", listOf(TextContent.Text("Text link"))),
                            PostContent.Quote(
                                "Quote from Name",
                                "1",
                                listOf(
                                    PostContent.Quote(
                                        "Quote from another Name",
                                        "1",
                                        listOf(
                                            PostContent.Quote(
                                                "Quote from another Name",
                                                "1",
                                                listOf(TextContent.Text("Text3"))
                                            ),
                                            TextContent.Text("Text answer2")
                                        )
                                    ),
                                    TextContent.Text("Text answer1")
                                )
                            ),
                            PostContent.Spoiler("Spoiler1", listOf(TextContent.Text("Text"))),
                            PostContent.Spoiler("Spoiler2", listOf(TextContent.Text("Text"))),
                            PostContent.Spoiler("Spoiler3", listOf(TextContent.Text("Text"))),
                        )
                    )
                )
            }
        }
    }
}
