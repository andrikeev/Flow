package flow.network.dto.topic

import kotlinx.serialization.Serializable

enum class ElementType {
    text,
    box,
    bold,
    italic,
    underscore,
    crossed,
    quote,
    code,
    spoiler,
    image,
    imageAligned,
    link,
    list,
    hr,
    br
}

enum class Alignment {
    start,
    top,
    end,
    bottom
}

@Serializable
abstract class PostElementDto(
    @Suppress("unused") val type: ElementType
)

data class Text(val value: String) : PostElementDto(ElementType.text) {
    override fun toString(): String = value
}

data class Box(val children: List<PostElementDto>) : PostElementDto(ElementType.box) {
    override fun toString(): String = "Box { $children }"
}

data class Bold(val children: List<PostElementDto>) : PostElementDto(ElementType.bold) {
    override fun toString(): String = "Bold { $children }"
}

data class Italic(val children: List<PostElementDto>) : PostElementDto(ElementType.italic) {
    override fun toString(): String = "Italic { $children }"
}

data class Underscore(val children: List<PostElementDto>) : PostElementDto(ElementType.underscore) {
    override fun toString(): String = "Underscore { $children }"
}

data class Crossed(val children: List<PostElementDto>) : PostElementDto(ElementType.crossed) {
    override fun toString(): String = "Crossed { $children }"
}

data class Quote(val title: String, val id: String, val children: List<PostElementDto>) :
    PostElementDto(ElementType.quote) {
    override fun toString(): String = "Quote($title)<id> { $children }"
}

data class Code(val title: String, val children: List<PostElementDto>) : PostElementDto(ElementType.code) {
    override fun toString(): String = "Code($title) { $children }"
}

data class Spoiler(val title: String, val children: List<PostElementDto>) : PostElementDto(ElementType.spoiler) {
    override fun toString(): String = "Spoiler($title) { $children }"
}

data class Image(val src: String) : PostElementDto(ElementType.image) {
    override fun toString(): String = "Image { $src }"
}

data class ImageAligned(val src: String, val alignment: Alignment) : PostElementDto(ElementType.imageAligned) {
    override fun toString(): String = "Image { $src <$alignment> }"
}

data class Link(val src: String, val children: List<PostElementDto>) : PostElementDto(ElementType.link) {
    override fun toString(): String = "Link($src) { $children }"
}

data class UList(val children: List<PostElementDto>) : PostElementDto(ElementType.list) {
    override fun toString(): String = "UList { $children }"
}

class Hr : PostElementDto(ElementType.hr) {
    override fun toString(): String = "<hr>"
}

class Br : PostElementDto(ElementType.br) {
    override fun toString(): String = "<br>"
}
