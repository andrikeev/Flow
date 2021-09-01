package me.rutrackersearch.domain.entity.topic

sealed interface Content

sealed interface TextContent : Content {
    data class Text(val text: String) : TextContent
    data class Default(val children: List<TextContent>) : TextContent
    sealed interface StyledText : TextContent {
        val children: List<TextContent>

        data class Bold(override val children: List<TextContent>) : StyledText
        data class Italic(override val children: List<TextContent>) : StyledText
        data class Underscore(override val children: List<TextContent>) : StyledText
        data class Crossed(override val children: List<TextContent>) : StyledText
    }
}

sealed interface PostContent : Content {
    data class Default(val children: List<Content>) : PostContent
    data class Box(val children: List<Content>) : PostContent
    data class Quote(val title: String, val id: String, val children: List<Content>) : PostContent
    data class Code(val title: String, val children: List<Content>) : PostContent
    data class Spoiler(val title: String, val children: List<Content>) : PostContent
    data class Image(val src: String, val alignment: Alignment? = null) : PostContent
    data class TorrentMainImage(val src: String) : PostContent
    data class Link(val src: String, val children: List<Content>) : PostContent
    data class PostList(val children: List<Content>) : PostContent
    object Hr : PostContent
}

@Suppress("unused", "EnumEntryName")
enum class Alignment {
    start, top, end, bottom
}
