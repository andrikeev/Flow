package me.rutrackersearch.data.converters

import android.net.Uri
import me.rutrackersearch.data.converters.ElementType.bold
import me.rutrackersearch.data.converters.ElementType.box
import me.rutrackersearch.data.converters.ElementType.br
import me.rutrackersearch.data.converters.ElementType.code
import me.rutrackersearch.data.converters.ElementType.crossed
import me.rutrackersearch.data.converters.ElementType.hr
import me.rutrackersearch.data.converters.ElementType.image
import me.rutrackersearch.data.converters.ElementType.imageAligned
import me.rutrackersearch.data.converters.ElementType.italic
import me.rutrackersearch.data.converters.ElementType.link
import me.rutrackersearch.data.converters.ElementType.list
import me.rutrackersearch.data.converters.ElementType.quote
import me.rutrackersearch.data.converters.ElementType.spoiler
import me.rutrackersearch.data.converters.ElementType.text
import me.rutrackersearch.data.converters.ElementType.underscore
import me.rutrackersearch.data.converters.ElementType.valueOf
import me.rutrackersearch.data.converters.PostElement.Bold
import me.rutrackersearch.data.converters.PostElement.Box
import me.rutrackersearch.data.converters.PostElement.Br
import me.rutrackersearch.data.converters.PostElement.Code
import me.rutrackersearch.data.converters.PostElement.Crossed
import me.rutrackersearch.data.converters.PostElement.Hr
import me.rutrackersearch.data.converters.PostElement.Image
import me.rutrackersearch.data.converters.PostElement.ImageAligned
import me.rutrackersearch.data.converters.PostElement.Italic
import me.rutrackersearch.data.converters.PostElement.Link
import me.rutrackersearch.data.converters.PostElement.Quote
import me.rutrackersearch.data.converters.PostElement.Spoiler
import me.rutrackersearch.data.converters.PostElement.Text
import me.rutrackersearch.data.converters.PostElement.UList
import me.rutrackersearch.data.converters.PostElement.Underscore
import me.rutrackersearch.domain.entity.topic.Alignment
import me.rutrackersearch.domain.entity.topic.Content
import me.rutrackersearch.domain.entity.topic.Post
import me.rutrackersearch.domain.entity.topic.PostContent
import me.rutrackersearch.domain.entity.topic.TextContent
import me.rutrackersearch.domain.entity.topic.TorrentDescription
import org.json.JSONObject

fun JSONObject.parseTorrentDescription(): TorrentDescription {
    return TorrentDescription(parseContent())
}

fun JSONObject.parsePost(): Post {
    return Post(
        id = getString("id"),
        author = getJSONObject("author").parseAuthor(),
        date = getString("date"),
        content = parseContent(),
    )
}

private fun JSONObject.parsePostElements(): List<PostElement> {
    val jsonArray = getJSONArray("children")
    val children = mutableListOf<PostElement>()
    for (i in 0 until jsonArray.length()) {
        val item = jsonArray.getJSONObject(i)
        item.parsePostElement()?.let { postElement ->
            children.add(postElement)
        }
    }
    return children
}

private fun JSONObject.parsePostElement(): PostElement? {
    val type = try {
        valueOf(getString("type"))
    } catch (e: IllegalArgumentException) {
        null
    }
    return when (type) {
        text -> Text(getString("value"))
        box -> Box(parsePostElements())
        bold -> Bold(parsePostElements())
        italic -> Italic(parsePostElements())
        underscore -> Underscore(parsePostElements())
        crossed -> Crossed(parsePostElements())
        quote -> Quote(getString("title"), getString("id"), parsePostElements())
        code -> Code(getString("title"), parsePostElements())
        spoiler -> Spoiler(getString("title"), parsePostElements())
        image -> Image(getString("src"))
        imageAligned -> ImageAligned(getString("src"), requireNotNull(optionalEnum<Alignment>("alignment")))
        link -> Link(getString("src").toUrl(), parsePostElements())
        list -> UList(parsePostElements())
        hr -> Hr
        br -> Br
        null -> null
    }
}

private fun String.toUrl(): String {
    return try {
        val uri = Uri.parse(this)
        val builder = uri.buildUpon()
        if (uri.scheme == null) {
            builder.scheme("https")
        }
        if (uri.host == null) {
            builder.authority("rutracker.org")
            builder.path("forum/${uri.path}")
        }
        builder.build().toString()
    } catch (e: Exception) {
        this
    }
}

private fun JSONObject.parseContent(): Content {
    val postElements = parsePostElements()
    val wrappers = mutableListOf<ContentWrapper>()
    postElements.forEach { postElement ->
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

@Suppress("EnumEntryName")
private enum class ElementType {
    text, box, bold, italic,
    underscore, crossed, quote,
    code, spoiler, image, imageAligned,
    link, list, hr, br
}

private sealed interface PostElement {
    data class Text(
        val value: String,
    ) : PostElement

    data class Box(
        val children: List<PostElement>,
    ) : PostElement

    data class Bold(
        val children: List<PostElement>,
    ) : PostElement

    data class Italic(
        val children: List<PostElement>,
    ) : PostElement

    data class Underscore(
        val children: List<PostElement>,
    ) : PostElement

    data class Crossed(
        val children: List<PostElement>,
    ) : PostElement

    data class Quote(
        val title: String,
        val id: String,
        val children: List<PostElement>,
    ) : PostElement

    data class Code(
        val title: String,
        val children: List<PostElement>,
    ) : PostElement

    data class Spoiler(
        val title: String,
        val children: List<PostElement>,
    ) : PostElement

    data class Image(
        val src: String,
    ) : PostElement

    data class ImageAligned(
        val src: String,
        val alignment: Alignment,
    ) : PostElement

    data class Link(
        val src: String,
        val children: List<PostElement>,
    ) : PostElement

    data class UList(
        val children: List<PostElement>,
    ) : PostElement

    object Hr : PostElement

    object Br : PostElement
}
