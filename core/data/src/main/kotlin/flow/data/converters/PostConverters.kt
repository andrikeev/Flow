package flow.data.converters

import flow.models.topic.Content
import flow.models.topic.Post
import flow.models.topic.PostContent
import flow.models.topic.TextContent
import flow.models.topic.TorrentDescription
import flow.network.dto.topic.Alignment
import flow.network.dto.topic.Bold
import flow.network.dto.topic.Box
import flow.network.dto.topic.Br
import flow.network.dto.topic.Code
import flow.network.dto.topic.Crossed
import flow.network.dto.topic.Hr
import flow.network.dto.topic.Image
import flow.network.dto.topic.ImageAligned
import flow.network.dto.topic.Italic
import flow.network.dto.topic.Link
import flow.network.dto.topic.PostDto
import flow.network.dto.topic.PostElementDto
import flow.network.dto.topic.Quote
import flow.network.dto.topic.Spoiler
import flow.network.dto.topic.Text
import flow.network.dto.topic.TorrentDescriptionDto
import flow.network.dto.topic.UList
import flow.network.dto.topic.Underscore

internal fun TorrentDescriptionDto.toTorrentDescription() =
    buildList<ContentWrapper> {
        children.forEach { postElement ->
            if (postElement.hasOnlyText()) {
                when (val lastWrapper = lastOrNull()) {
                    is TextContentWrapper -> lastWrapper.items.add(postElement)
                    else -> add(TextContentWrapper(mutableListOf(postElement)))
                }
            } else {
                when (val lastWrapper = lastOrNull()) {
                    is PostContentWrapper -> lastWrapper.items.add(postElement)
                    else -> add(PostContentWrapper(mutableListOf(postElement)))
                }
            }
        }
    }
        .map { wrapper ->
            when (wrapper) {
                is TextContentWrapper -> TextContent.Default(wrapper.items.toTextContent())
                is PostContentWrapper -> PostContent.Default(wrapper.items.toContent())
            }
        }
        .let(PostContent::Default)
        .let(::TorrentDescription)

internal fun List<PostDto>.toPosts() = map(PostDto::toPost)

private fun PostDto.toPost() = Post(
    id = id,
    author = author.toAuthor(),
    date = date,
    content = children.toContent().let(PostContent::Default),
)

private sealed interface ContentWrapper {
    val items: List<PostElementDto>
}

private data class TextContentWrapper(override val items: MutableList<PostElementDto>) : ContentWrapper
private data class PostContentWrapper(override val items: MutableList<PostElementDto>) : ContentWrapper

private fun List<PostElementDto>.toContent(): List<Content> = map { element ->
    when (element) {
        is Br -> TextContent.Text("\n")
        is Text -> TextContent.Text(element.value)
        is Italic -> {
            if (element.hasOnlyText()) {
                TextContent.StyledText.Italic(element.children.toTextContent())
            } else {
                PostContent.Default(element.children.toContent())
            }
        }

        is Bold -> {
            if (element.hasOnlyText()) {
                TextContent.StyledText.Bold(element.children.toTextContent())
            } else {
                PostContent.Default(element.children.toContent())
            }
        }

        is Underscore -> {
            if (element.hasOnlyText()) {
                TextContent.StyledText.Underscore(element.children.toTextContent())
            } else {
                PostContent.Default(element.children.toContent())
            }
        }

        is Crossed -> {
            if (element.hasOnlyText()) {
                TextContent.StyledText.Crossed(element.children.toTextContent())
            } else {
                PostContent.Default(element.children.toContent())
            }
        }

        is Box -> PostContent.Box(element.children.toContent())
        is Code -> PostContent.Code(element.title, element.children.toContent())
        is Image -> PostContent.Image(element.src)
        is ImageAligned -> when (element.alignment) {
            Alignment.Start -> PostContent.Image(element.src, flow.models.topic.Alignment.start)
            Alignment.Top -> PostContent.Image(element.src, flow.models.topic.Alignment.top)
            Alignment.End -> PostContent.TorrentMainImage(element.src)
            Alignment.Bottom -> PostContent.Image(element.src, flow.models.topic.Alignment.bottom)
        }

        is Link -> PostContent.Link(element.src, element.children.toContent())
        is Quote -> PostContent.Quote(element.title, element.id, element.children.toContent())
        is Spoiler -> PostContent.Spoiler(element.title, element.children.toContent())
        is UList -> PostContent.PostList(element.children.toContent())
        is Hr -> PostContent.Hr
        else -> PostContent.Default(emptyList())
    }
}

private fun List<PostElementDto>.toTextContent(): List<TextContent> = mapNotNull { element ->
    when (element) {
        is Text -> TextContent.Text(element.value)
        is Br -> TextContent.Text("\n")
        is Italic -> TextContent.StyledText.Italic(element.children.toTextContent())
        is Bold -> TextContent.StyledText.Bold(element.children.toTextContent())
        is Underscore -> TextContent.StyledText.Underscore(element.children.toTextContent())
        is Crossed -> TextContent.StyledText.Crossed(element.children.toTextContent())
        else -> null
    }
}

private fun PostElementDto.hasOnlyText(): Boolean = when (this) {
    is Br -> true
    is Text -> true
    is Italic -> children.all(PostElementDto::hasOnlyText)
    is Bold -> children.all(PostElementDto::hasOnlyText)
    is Crossed -> children.all(PostElementDto::hasOnlyText)
    is Underscore -> children.all(PostElementDto::hasOnlyText)
    else -> false
}
