package me.rutrackersearch.network.parsers

import me.rutrackersearch.models.topic.Alignment
import me.rutrackersearch.models.topic.Content
import me.rutrackersearch.models.topic.PostContent
import me.rutrackersearch.models.topic.TextContent
import me.rutrackersearch.network.parsers.PostElement.Bold
import me.rutrackersearch.network.parsers.PostElement.Box
import me.rutrackersearch.network.parsers.PostElement.Br
import me.rutrackersearch.network.parsers.PostElement.Code
import me.rutrackersearch.network.parsers.PostElement.Crossed
import me.rutrackersearch.network.parsers.PostElement.Hr
import me.rutrackersearch.network.parsers.PostElement.Image
import me.rutrackersearch.network.parsers.PostElement.ImageAligned
import me.rutrackersearch.network.parsers.PostElement.Italic
import me.rutrackersearch.network.parsers.PostElement.Link
import me.rutrackersearch.network.parsers.PostElement.Quote
import me.rutrackersearch.network.parsers.PostElement.Spoiler
import me.rutrackersearch.network.parsers.PostElement.Text
import me.rutrackersearch.network.parsers.PostElement.UList
import me.rutrackersearch.network.parsers.PostElement.Underscore

internal fun List<PostElement>.parseContent(): Content {
    val wrappers = mutableListOf<ContentWrapper>()
    forEach { postElement ->
        val lastWrapper = wrappers.lastOrNull()
        if (postElement.hasOnlyText()) {
            when (lastWrapper) {
                is TextContentWrapper -> lastWrapper.items.add(postElement)
                else -> wrappers.add(TextContentWrapper(mutableListOf(postElement)))
            }
        } else {
            when (lastWrapper) {
                is PostContentWrapper -> lastWrapper.items.add(postElement)
                else -> wrappers.add(PostContentWrapper(mutableListOf(postElement)))
            }
        }
    }
    return wrappers.map {
        when (it) {
            is TextContentWrapper -> {
                TextContent.Default(it.items.toTextContent())
            }
            is PostContentWrapper -> {
                PostContent.Default(it.items.toContent())
            }
        }
    }.let(PostContent::Default)
}

private sealed interface ContentWrapper {
    val items: List<PostElement>
}

private data class TextContentWrapper(override val items: MutableList<PostElement>) : ContentWrapper
private data class PostContentWrapper(override val items: MutableList<PostElement>) : ContentWrapper

private fun List<PostElement>.toContent(): List<Content> {
    return map { element ->
        when (element) {
            Br -> TextContent.Text("\n")
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
            is ImageAligned -> {
                if (element.alignment == Alignment.end) {
                    PostContent.TorrentMainImage(element.src)
                } else {
                    PostContent.Image(element.src, element.alignment)
                }
            }
            is Link -> PostContent.Link(element.src, element.children.toContent())
            is Quote -> PostContent.Quote(element.title, element.id, element.children.toContent())
            is Spoiler -> PostContent.Spoiler(element.title, element.children.toContent())
            is UList -> PostContent.PostList(element.children.toContent())
            Hr -> PostContent.Hr
        }
    }
}

private fun List<PostElement>.toTextContent(): List<TextContent> {
    return mapNotNull { element ->
        when (element) {
            is Text -> TextContent.Text(element.value)
            Br -> TextContent.Text("\n")
            is Italic -> TextContent.StyledText.Italic(element.children.toTextContent())
            is Bold -> TextContent.StyledText.Bold(element.children.toTextContent())
            is Underscore -> TextContent.StyledText.Underscore(element.children.toTextContent())
            is Crossed -> TextContent.StyledText.Crossed(element.children.toTextContent())
            else -> null
        }
    }
}

private fun PostElement.hasOnlyText(): Boolean = when (this) {
    Br -> true
    is Text -> true
    is Italic -> children.all(PostElement::hasOnlyText)
    is Bold -> children.all(PostElement::hasOnlyText)
    is Crossed -> children.all(PostElement::hasOnlyText)
    is Underscore -> children.all(PostElement::hasOnlyText)
    else -> false
}
