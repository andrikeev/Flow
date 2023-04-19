package flow.network.dto.topic

import kotlinx.serialization.Serializable

@Serializable
enum class Alignment { Start, Top, End, Bottom; }

@Serializable
sealed interface PostElementDto

@Serializable
data class Text(val value: String) : PostElementDto {
    override fun toString(): String = value
}

@Serializable
data class Box(val children: List<PostElementDto>) : PostElementDto {
    override fun toString(): String = "Box { $children }"
}

@Serializable
data class Bold(val children: List<PostElementDto>) : PostElementDto {
    override fun toString(): String = "Bold { $children }"
}

@Serializable
data class Italic(val children: List<PostElementDto>) : PostElementDto {
    override fun toString(): String = "Italic { $children }"
}

@Serializable
data class Underscore(val children: List<PostElementDto>) : PostElementDto {
    override fun toString(): String = "Underscore { $children }"
}

@Serializable
data class Crossed(val children: List<PostElementDto>) : PostElementDto {
    override fun toString(): String = "Crossed { $children }"
}

@Serializable
data class Quote(
    val title: String,
    val id: String,
    val children: List<PostElementDto>,
) : PostElementDto {
    override fun toString(): String = "Quote($title)<id> { $children }"
}

@Serializable
data class Code(val title: String, val children: List<PostElementDto>) : PostElementDto {
    override fun toString(): String = "Code($title) { $children }"
}

@Serializable
data class Spoiler(val title: String, val children: List<PostElementDto>) : PostElementDto {
    override fun toString(): String = "Spoiler($title) { $children }"
}

@Serializable
data class Image(val src: String) : PostElementDto {
    override fun toString(): String = "Image { $src }"
}

@Serializable
data class ImageAligned(val src: String, val alignment: Alignment) : PostElementDto {
    override fun toString(): String = "Image { $src <$alignment> }"
}

@Serializable
data class Link(val src: String, val children: List<PostElementDto>) : PostElementDto {
    override fun toString(): String = "Link($src) { $children }"
}

@Serializable
data class UList(val children: List<PostElementDto>) : PostElementDto {
    override fun toString(): String = "UList { $children }"
}

@Serializable
object Hr : PostElementDto {
    override fun toString(): String = "<hr>"
}

@Serializable
object Br : PostElementDto {
    override fun toString(): String = "<br>"
}
