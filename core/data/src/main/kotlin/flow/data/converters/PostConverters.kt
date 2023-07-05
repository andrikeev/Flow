package flow.data.converters

import flow.models.topic.ColorValue
import flow.models.topic.Content
import flow.models.topic.ContentColumn
import flow.models.topic.ContentRow
import flow.models.topic.Post
import flow.models.topic.PostContent
import flow.models.topic.TextAlignment
import flow.models.topic.TextContent
import flow.network.dto.topic.Align
import flow.network.dto.topic.Bold
import flow.network.dto.topic.Box
import flow.network.dto.topic.Br
import flow.network.dto.topic.Code
import flow.network.dto.topic.Color
import flow.network.dto.topic.Crossed
import flow.network.dto.topic.Hr
import flow.network.dto.topic.Image
import flow.network.dto.topic.ImageAligned
import flow.network.dto.topic.Italic
import flow.network.dto.topic.Link
import flow.network.dto.topic.PostBr
import flow.network.dto.topic.PostDto
import flow.network.dto.topic.PostElementDto
import flow.network.dto.topic.Quote
import flow.network.dto.topic.Size
import flow.network.dto.topic.Spoiler
import flow.network.dto.topic.Text
import flow.network.dto.topic.UList
import flow.network.dto.topic.Underscore

internal fun List<PostDto>.toPosts() = map(PostDto::toPost)

private fun PostDto.toPost() = Post(
    id = id,
    author = author.toAuthor(),
    date = date,
    content = children.toContent(),
)

private fun List<PostElementDto>.toContent(): Content {
    val column = mutableListOf<Content>()
    var row = mutableListOf<Content>()

    fun MutableList<Content>.isTextRow(): Boolean {
        return all { it is TextContent }
    }

    fun MutableList<Content>.toContent(): Content {
        val groups = mutableListOf<Content>()
        var group = mutableListOf<Content>()
        forEach { content ->
            when {
                content is TextContent && !group.isTextRow() -> {
                    groups.add(ContentRow(group))
                    group = mutableListOf()
                }
                content !is TextContent && group.isTextRow() -> {
                    groups.add(TextContent.TextRow(group.filterIsInstance<TextContent>()))
                    group = mutableListOf()
                }
            }
            group.add(content)
        }
        if (group.isNotEmpty()) {
            groups.add(
                if (group.isTextRow()) {
                    TextContent.TextRow(group.filterIsInstance<TextContent>())
                } else {
                    ContentRow(group)
                },
            )
        }

        return if (groups.size == 1) {
            groups.first()
        } else {
            ContentRow(groups)
        }
    }

    fun addBreak() {
        when {
            row.isEmpty() -> Unit
            row.size == 1 -> column.add(row.first())
            else -> column.add(row.toContent())
        }
        row = mutableListOf()
    }

    fun addSpacer() {
        addBreak()
        if (
            column.lastOrNull() != PostContent.Spacer &&
            column.lastOrNull() != PostContent.Divider
        ) {
            column.add(PostContent.Spacer)
        }
    }

    fun addDivider() {
        addBreak()
        if (column.lastOrNull() != PostContent.Divider) {
            if (column.lastOrNull() == PostContent.Spacer) {
                column.removeLast()
            }
            column.add(PostContent.Divider)
        }
    }

    fun addContent(content: Content) {
        row.add(content)
    }

    forEach { postElementDto ->
        when (postElementDto) {
            is Align -> addContent(
                PostContent.Align(
                    postElementDto.textAlignment,
                    postElementDto.children.toContent(),
                ),
            )

            is Bold -> addContent(
                if (postElementDto.isTextElement()) {
                    TextContent.Bold(postElementDto.children.toTextContent())
                } else {
                    PostContent.Bold(postElementDto.children.toContent())
                },
            )

            is Box -> addContent(PostContent.Box(postElementDto.children.toContent()))

            is Br -> addBreak()

            is Code -> addContent(
                PostContent.Code(
                    postElementDto.title,
                    postElementDto.children.toContent(),
                ),
            )

            is Color -> addContent(
                if (postElementDto.isTextElement()) {
                    TextContent.Color(
                        postElementDto.textColor,
                        postElementDto.children.toTextContent(),
                    )
                } else {
                    PostContent.Color(
                        postElementDto.textColor,
                        postElementDto.children.toContent(),
                    )
                },
            )

            is Crossed -> addContent(
                if (postElementDto.isTextElement()) {
                    TextContent.Crossed(postElementDto.children.toTextContent())
                } else {
                    PostContent.Crossed(postElementDto.children.toContent())
                },
            )

            is Hr -> addDivider()

            is Image -> addContent(PostContent.Image(postElementDto.src))

            is ImageAligned -> addBreak()

            is Italic -> addContent(
                if (postElementDto.isTextElement()) {
                    TextContent.Italic(postElementDto.children.toTextContent())
                } else {
                    PostContent.Italic(postElementDto.children.toContent())
                },
            )

            is Link -> addContent(
                if (postElementDto.isTextElement()) {
                    TextContent.Link(postElementDto.src, postElementDto.children.toTextContent())
                } else {
                    PostContent.Link(postElementDto.src, postElementDto.children.toContent())
                },
            )

            is PostBr -> addSpacer()

            is Quote -> addContent(
                PostContent.Quote(
                    postElementDto.title,
                    postElementDto.children.toContent(),
                ),
            )

            is Size -> addContent(
                PostContent.Size(
                    postElementDto.size.coerceIn(14, 20),
                    postElementDto.children.toContent(),
                ),
            )

            is Spoiler -> addContent(
                PostContent.Spoiler(
                    postElementDto.title,
                    postElementDto.children.toContent(),
                ),
            )

            is Text -> addContent(TextContent.Text(postElementDto.value))

            is UList -> addContent(PostContent.PostList(postElementDto.children.toContent()))

            is Underscore -> addContent(
                if (postElementDto.isTextElement()) {
                    TextContent.Underscore(postElementDto.children.toTextContent())
                } else {
                    PostContent.Underscore(postElementDto.children.toContent())
                },
            )
        }
    }
    addBreak()

    return if (column.size == 1) {
        column.first()
    } else {
        ContentColumn(column)
    }
}

private fun List<PostElementDto>.toTextContent(): TextContent {
    val content = mapNotNull { element ->
        when (element) {
            is Bold -> TextContent.Bold(element.children.toTextContent())
            is Color -> TextContent.Color(element.textColor, element.children.toTextContent())
            is Crossed -> TextContent.Crossed(element.children.toTextContent())
            is Italic -> TextContent.Italic(element.children.toTextContent())
            is Link -> TextContent.Link(element.src, element.children.toTextContent())
            is Text -> TextContent.Text(element.value)
            is Underscore -> TextContent.Underscore(element.children.toTextContent())
            else -> null
        }
    }
    return if (content.size == 1) {
        content.first()
    } else {
        TextContent.TextRow(content)
    }
}

private val Color.textColor: ColorValue
    get() = when (val color = this.color) {
        is flow.network.dto.topic.ColorValue.Hex -> ColorValue.Hex(color.hex)
        is flow.network.dto.topic.ColorValue.Name -> ColorValue.Name(color.name)
    }

private val Align.textAlignment: TextAlignment
    get() = when (alignment) {
        flow.network.dto.topic.TextAlignment.Left -> TextAlignment.Left
        flow.network.dto.topic.TextAlignment.Right -> TextAlignment.Right
        flow.network.dto.topic.TextAlignment.Center -> TextAlignment.Center
        flow.network.dto.topic.TextAlignment.Justify -> TextAlignment.Justify
    }

private fun PostElementDto.isTextElement(): Boolean = when (this) {
    is Bold -> children.all(PostElementDto::isTextElement)
    is Color -> children.all(PostElementDto::isTextElement)
    is Crossed -> children.all(PostElementDto::isTextElement)
    is Italic -> children.all(PostElementDto::isTextElement)
    is Link -> children.all(PostElementDto::isTextElement)
    is Text -> true
    is Underscore -> children.all(PostElementDto::isTextElement)
    else -> false
}
