package flow.network.dto.topic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Alignment { Start, Top, End, Bottom; }

@Serializable
enum class TextAlignment { Left, Right, Center, Justify; }

@Serializable
sealed interface ColorValue {
    @Serializable
    @SerialName("Hex")
    data class Hex(val hex: Long) : ColorValue

    @Serializable
    @SerialName("Name")
    data class Name(val name: String) : ColorValue
}

@Serializable
sealed interface PostElementDto

@Serializable
@SerialName("Text")
data class Text(val value: String) : PostElementDto

@Serializable
@SerialName("Box")
data class Box(val children: List<PostElementDto>) : PostElementDto

@Serializable
@SerialName("Align")
data class Align(
    val alignment: TextAlignment,
    val children: List<PostElementDto>,
) : PostElementDto

@Serializable
@SerialName("Size")
data class Size(
    val size: Int,
    val children: List<PostElementDto>,
) : PostElementDto

@Serializable
@SerialName("Color")
data class Color(
    val color: ColorValue,
    val children: List<PostElementDto>,
) : PostElementDto

@Serializable
@SerialName("Bold")
data class Bold(val children: List<PostElementDto>) : PostElementDto

@Serializable
@SerialName("Italic")
data class Italic(val children: List<PostElementDto>) : PostElementDto

@Serializable
@SerialName("Underscore")
data class Underscore(val children: List<PostElementDto>) : PostElementDto

@Serializable
@SerialName("Crossed")
data class Crossed(val children: List<PostElementDto>) : PostElementDto

@Serializable
@SerialName("Quote")
data class Quote(
    val title: String,
    val id: String,
    val children: List<PostElementDto>,
) : PostElementDto

@Serializable
@SerialName("Code")
data class Code(
    val title: String,
    val children: List<PostElementDto>,
) : PostElementDto

@Serializable
@SerialName("Spoiler")
data class Spoiler(
    val title: String,
    val children: List<PostElementDto>,
) : PostElementDto

@Serializable
@SerialName("Image")
data class Image(val src: String) : PostElementDto

@Serializable
@SerialName("ImageAligned")
data class ImageAligned(
    val src: String,
    val alignment: Alignment,
) : PostElementDto

@Serializable
@SerialName("Link")
data class Link(
    val src: String,
    val children: List<PostElementDto>,
) : PostElementDto

@Serializable
@SerialName("List")
data class UList(val children: List<PostElementDto>) : PostElementDto

@Serializable
@SerialName("Hr")
object Hr : PostElementDto

@Serializable
@SerialName("Br")
object Br : PostElementDto

@Serializable
@SerialName("PostBr")
object PostBr : PostElementDto
