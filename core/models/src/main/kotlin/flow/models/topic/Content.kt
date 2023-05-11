package flow.models.topic

sealed interface Content

data class ContentColumn(val children: List<Content>) : Content

data class ContentRow(val children: List<Content>) : Content

sealed interface TextContent : Content {
    data class Text(val text: String) : TextContent
    data class TextRow(val children: List<TextContent>) : TextContent
    data class Bold(val content: TextContent) : TextContent
    data class Italic(val content: TextContent) : TextContent
    data class Underscore(val content: TextContent) : TextContent
    data class Crossed(val content: TextContent) : TextContent
    data class Color(val color: ColorValue, val content: TextContent) : TextContent
    data class Link(val src: String, val content: TextContent) : TextContent
}

sealed interface PostContent : Content {
    data class Align(
        val alignment: TextAlignment,
        val content: Content,
    ) : PostContent

    data class Bold(val content: Content) : PostContent

    data class Italic(val content: Content) : PostContent

    data class Underscore(val content: Content) : PostContent

    data class Crossed(val content: Content) : PostContent

    data class Size(
        val size: Int,
        val content: Content,
    ) : PostContent

    data class Color(
        val color: ColorValue,
        val content: Content,
    ) : PostContent

    data class Box(val content: Content) : PostContent

    data class Quote(
        val title: String,
        val content: Content,
    ) : PostContent

    data class Code(
        val title: String,
        val content: Content,
    ) : PostContent

    data class Spoiler(
        val title: String,
        val content: Content,
    ) : PostContent

    data class Image(val src: String) : PostContent

    data class Link(
        val src: String,
        val content: Content,
    ) : PostContent

    data class PostList(val content: Content) : PostContent

    object Divider : PostContent

    object Spacer : PostContent
}

enum class TextAlignment { Left, Right, Center, Justify; }

sealed interface ColorValue {
    data class Hex(val hex: Long) : ColorValue
    data class Name(val name: String) : ColorValue
}
