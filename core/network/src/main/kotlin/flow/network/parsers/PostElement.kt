package flow.network.parsers

import flow.models.topic.Alignment

sealed interface PostElement {
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
