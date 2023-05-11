package flow.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import flow.designsystem.component.ClickableText
import flow.designsystem.component.Divider
import flow.designsystem.component.ExpandCollapseIcon
import flow.designsystem.component.LocalSnackbarHostState
import flow.designsystem.component.ProvideTextStyle
import flow.designsystem.component.SnackbarHostState
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.ThemePreviews
import flow.designsystem.component.rememberExpandState
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.models.topic.ColorValue
import flow.models.topic.Content
import flow.models.topic.ContentColumn
import flow.models.topic.ContentRow
import flow.models.topic.PostContent
import flow.models.topic.TextAlignment
import flow.models.topic.TextContent
import flow.ui.R
import flow.ui.platform.LocalOpenLinkHandler
import flow.ui.platform.OpenLinkHandler
import kotlinx.coroutines.launch

@Composable
fun Post(
    modifier: Modifier = Modifier,
    content: Content,
) = ProvideTextStyle(AppTheme.typography.bodyMedium) {
    Content(content, modifier)
}

@Composable
private fun Content(
    content: List<Content>,
    modifier: Modifier = Modifier,
) {
    content.forEach { Content(it, modifier = modifier) }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Content(
    content: Content,
    modifier: Modifier = Modifier,
) = Box(modifier = modifier) {
    when (content) {
        is ContentColumn -> Column { Content(content.children) }
        is ContentRow -> FlowRow(verticalAlignment = Alignment.CenterVertically) {
            Content(content.children)
        }
        is TextContent -> TextContent(content)
        is PostContent -> PostContent(content)
    }
}

@Composable
private fun TextContent(
    content: Content,
    modifier: Modifier = Modifier,
) {
    val openLinkHandler = LocalOpenLinkHandler.current
    val snackbarState = LocalSnackbarHostState.current
    val coroutinesScope = rememberCoroutineScope()
    val linkErrorMessage = stringResource(R.string.error_open_url)
    val text = buildAnnotatedString { TextContent(content) }
    ClickableText(
        modifier = modifier.padding(vertical = AppTheme.spaces.extraSmall),
        text = text,
        onClick = { offset ->
            text.getStringAnnotations(
                tag = "URL",
                start = offset,
                end = offset,
            )
                .firstOrNull()
                ?.let {
                    try {
                        openLinkHandler.openLink(it.item)
                    } catch (e: Throwable) {
                        coroutinesScope.launch { snackbarState.showSnackbar(linkErrorMessage) }
                    }
                }
        }
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun AnnotatedString.Builder.TextContent(content: Content) {
    when (content) {
        is TextContent.Text -> append(content.text)

        is TextContent.TextRow -> content.children.forEach { TextContent(it) }

        is TextContent.Link -> append(buildAnnotatedString {
            pushStyle(
                SpanStyle(
                    color = AppTheme.colors.primary,
                    textDecoration = TextDecoration.Underline,
                )
            )
            pushUrlAnnotation(UrlAnnotation(content.src))
            pushStringAnnotation(
                tag = "URL",
                annotation = content.src,
            )
            TextContent(content.content)
        })

        is TextContent.Bold -> append(buildAnnotatedString {
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            TextContent(content.content)
        })

        is TextContent.Crossed -> append(buildAnnotatedString {
            pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
            TextContent(content.content)
        })

        is TextContent.Italic -> append(buildAnnotatedString {
            pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
            TextContent(content.content)
        })

        is TextContent.Underscore -> append(buildAnnotatedString {
            pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            TextContent(content.content)
        })

        is TextContent.Color -> append(buildAnnotatedString {
            pushStyle(SpanStyle(color = AppTheme.colors.primary))
            TextContent(content.content)
        })

        else -> Unit
    }
}

@Composable
private fun PostContent(content: Content) {
    when (content) {
        is PostContent.Align -> Align(
            alignment = content.alignment,
            content = content.content,
        )

        is PostContent.Bold -> ProvideTextStyle(
            value = TextStyle(fontWeight = FontWeight.Bold),
            content = { Content(content.content) },
        )

        is PostContent.Box -> Box(content.content)

        is PostContent.Code -> Code(content.content)

        is PostContent.Color -> ProvideTextStyle(
            value = TextStyle(color = AppTheme.colors.primary),
            content = { Content(content.content) },
        )

        is PostContent.Crossed -> ProvideTextStyle(
            value = TextStyle(textDecoration = TextDecoration.LineThrough),
            content = { Content(content.content) },
        )

        is PostContent.Divider -> Divider(
            modifier = Modifier.padding(vertical = AppTheme.spaces.medium)
        )

        is PostContent.Image -> Image(content.src)

        is PostContent.Italic -> ProvideTextStyle(
            value = TextStyle(fontStyle = FontStyle.Italic),
            content = { Content(content.content) },
        )

        is PostContent.Link -> LinkItem(content.src, content.content)

        is PostContent.PostList -> Box(content.content)

        is PostContent.Quote -> Quote(content.title, content.content)

        is PostContent.Size -> ProvideTextStyle(
            value = TextStyle(
                fontSize = content.size.sp,
                lineHeight = (content.size * 1.3).sp
            ),
            content = { Content(content.content) },
        )

        is PostContent.Spacer -> Spacer(modifier = Modifier.height(AppTheme.spaces.medium))

        is PostContent.Spoiler -> Spoiler(content.title, content.content)

        is PostContent.Underscore -> ProvideTextStyle(
            value = TextStyle(textDecoration = TextDecoration.Underline),
            content = { Content(content.content) },
        )

        else -> Unit
    }
}

@Composable
private fun Align(
    alignment: TextAlignment,
    content: Content,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier
        .padding(vertical = AppTheme.spaces.small)
        .fillMaxWidth(),
    horizontalAlignment = when (alignment) {
        TextAlignment.Left -> Alignment.Start
        TextAlignment.Right -> Alignment.End
        TextAlignment.Center -> Alignment.CenterHorizontally
        TextAlignment.Justify -> Alignment.Start
    },
    content = { Content(content) },
)

@Composable
private fun Box(
    content: Content,
    modifier: Modifier = Modifier,
) = Surface(
    modifier = modifier.padding(vertical = AppTheme.spaces.mediumSmall),
    shape = AppTheme.shapes.small,
    tonalElevation = AppTheme.elevations.small,
    shadowElevation = AppTheme.elevations.small,
    content = { Content(content, modifier = Modifier.padding(AppTheme.spaces.medium)) },
)

@Composable
private fun Code(
    content: Content,
    modifier: Modifier = Modifier,
) = ProvideTextStyle(
    value = AppTheme.typography.bodySmall.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.Light,
        fontFamily = FontFamily.Monospace,
    ),
    content = { Box(content, modifier) },
)

@Composable
private fun Quote(
    title: String,
    content: Content,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier.padding(vertical = AppTheme.spaces.mediumSmall),
) {
    Text(
        modifier = Modifier.padding(
            start = AppTheme.spaces.small,
            bottom = AppTheme.spaces.extraSmall,
        ),
        text = title,
        style = AppTheme.typography.labelSmall,
    )
    ProvideTextStyle(
        value = AppTheme.typography.bodySmall,
        content = {
            Surface(
                shape = AppTheme.shapes.small,
                tonalElevation = AppTheme.elevations.small,
                shadowElevation = AppTheme.elevations.small,
                content = {
                    Content(
                        content = content,
                        modifier = Modifier.padding(AppTheme.spaces.medium),
                    )
                },
            )
        },
    )
}

@Composable
private fun Spoiler(
    title: String,
    content: Content,
    modifier: Modifier = Modifier,
) = Surface(
    modifier = modifier.padding(vertical = AppTheme.spaces.mediumSmall),
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
            content = {
                Content(
                    modifier = Modifier.padding(AppTheme.spaces.medium),
                    content = content,
                )
            },
        )
    }
}

@Composable
private fun Image(src: String) = RemoteImage(
    modifier = Modifier.fillMaxSize(),
    src = src,
    contentDescription = null,
)

@Composable
private fun LinkItem(
    src: String,
    content: Content,
    modifier: Modifier = Modifier,
) {
    val openLinkHandler = LocalOpenLinkHandler.current
    val snackbarState = LocalSnackbarHostState.current
    val coroutinesScope = rememberCoroutineScope()
    val linkErrorMessage = stringResource(R.string.error_open_url)
    Box(
        modifier = modifier
            .padding(AppTheme.spaces.small)
            .clickable {
                try {
                    openLinkHandler.openLink(src)
                } catch (e: Throwable) {
                    coroutinesScope.launch { snackbarState.showSnackbar(linkErrorMessage) }
                }
            }
            .clip(AppTheme.shapes.extraSmall),
        content = { Content(content) },
    )
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
                    modifier = Modifier
                        .padding(AppTheme.spaces.medium)
                        .fillMaxHeight(),
                    content = ContentColumn(
                        listOf(
                            PostContent.Size(
                                size = 29,
                                content = PostContent.Align(
                                    alignment = TextAlignment.Center,
                                    content = PostContent.Color(
                                        color = ColorValue.Name(name = "brown"),
                                        content = PostContent.Bold(
                                            content = ContentColumn(
                                                listOf(
                                                    TextContent.Text(text = "Экзорцист Ватикана"),
                                                    TextContent.Text(text = "The Pope 's Exorcist"),
                                                )
                                            )
                                        )
                                    )
                                )
                            ),
                            PostContent.Spacer,
                            PostContent.Divider,
                            PostContent.Spacer,
                            PostContent.Size(
                                size = 14,
                                content = ContentColumn(
                                    children = listOf(
                                        TextContent.TextRow(
                                            children = listOf(
                                                TextContent.Bold(TextContent.Text("Страна")),
                                                TextContent.Text(": США, Великобритания, Испания"),
                                            )
                                        ),
                                        TextContent.TextRow(
                                            children = listOf(
                                                TextContent.Bold(TextContent.Text("Студия")),
                                                TextContent.Text(": Screen Gems, 2.0 Entertainment, Loyola Productions")
                                            )
                                        ),
                                        TextContent.TextRow(
                                            children = listOf(
                                                TextContent.Bold(TextContent.Text("Жанр")),
                                                TextContent.Text(": ужасы")
                                            )
                                        ),
                                        PostContent.Spacer,
                                        TextContent.TextRow(
                                            children = listOf(
                                                TextContent.Bold(TextContent.Text("Перевод 1")),
                                                TextContent.Text(": Профессиональный (многоголосый закадровый) | "),
                                                TextContent.Color(
                                                    color = ColorValue.Name("green"),
                                                    content = TextContent.Bold(TextContent.Text("TVShows")),
                                                ),
                                                TextContent.Text(" // thx. "),
                                                TextContent.Color(
                                                    color = ColorValue.Name("gray"),
                                                    content = TextContent.Bold(TextContent.Text("FLEX"))
                                                )
                                            )
                                        ),
                                        TextContent.TextRow(
                                            children = listOf(
                                                TextContent.Bold(TextContent.Text("Перевод 2")),
                                                TextContent.Text(": Профессиональный (многоголосый закадровый) | "),
                                                TextContent.Color(
                                                    color = ColorValue.Name("green"),
                                                    content = TextContent.Bold(TextContent.Text("Jaskier"))
                                                ),
                                                TextContent.Text(" | "),
                                                TextContent.Color(
                                                    color = ColorValue.Name("blue"),
                                                    content = TextContent.Bold(TextContent.Text("text=18+")),
                                                )
                                            )
                                        ),
                                        PostContent.Spacer,
                                        TextContent.Bold(
                                            content = TextContent.TextRow(
                                                listOf(
                                                    TextContent.Text("|| "),
                                                    TextContent.Link(
                                                        src = "https://www.imdb.com/title/tt13375076/",
                                                        content = TextContent.Text("IMDb"),
                                                    ),
                                                    TextContent.Text(" || "),
                                                    TextContent.Link(
                                                        src = "https://www.kinopoisk.ru/film/4458585/",
                                                        content = TextContent.Text("КиноПоиск"),
                                                    ),
                                                    TextContent.Text(" || "),
                                                    TextContent.Link(
                                                        src = "https://yadi.sk/i/31As836dbYXFmw",
                                                        content = TextContent.Text("Скачать семпл"),
                                                    ),
                                                    TextContent.Text(" ||")
                                                )
                                            )
                                        ),
                                        PostContent.Quote(
                                            title = "Quote",
                                            content = TextContent.TextRow(
                                                listOf(
                                                    TextContent.Text("Quote "),
                                                    TextContent.Text("text row"),
                                                )
                                            )
                                        ),
                                        PostContent.Quote(
                                            title = "Quote",
                                            content = ContentRow(
                                                listOf(
                                                    TextContent.Text("Quote "),
                                                    TextContent.Text("content row"),
                                                )
                                            )
                                        ),
                                        PostContent.Spoiler(
                                            title = "Spoiler",
                                            content = TextContent.Text("Spoiler content")
                                        ),
                                        PostContent.Box(
                                            content = TextContent.Text("Box content")
                                        ),
                                        PostContent.Box(
                                            content = TextContent.TextRow(
                                                listOf(
                                                    TextContent.Text("Box "),
                                                    TextContent.Text("text row"),
                                                )
                                            )
                                        ),
                                        PostContent.Box(
                                            content = ContentRow(
                                                listOf(
                                                    TextContent.Text("Box "),
                                                    TextContent.Text("content row"),
                                                )
                                            )
                                        ),
                                        PostContent.Code(
                                            title = "Code",
                                            content = ContentRow(
                                                listOf(
                                                    TextContent.Text("Code "),
                                                    TextContent.Text("content row"),
                                                )
                                            )
                                        ),
                                        PostContent.Image("")
                                    ),
                                ),
                            )
                        )
                    )
                )
            }
        }
    }
}
