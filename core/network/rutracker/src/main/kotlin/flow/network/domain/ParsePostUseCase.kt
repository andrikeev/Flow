package flow.network.domain

import flow.network.dto.topic.Align
import flow.network.dto.topic.Alignment
import flow.network.dto.topic.Bold
import flow.network.dto.topic.Box
import flow.network.dto.topic.Br
import flow.network.dto.topic.Code
import flow.network.dto.topic.Color
import flow.network.dto.topic.ColorValue
import flow.network.dto.topic.Crossed
import flow.network.dto.topic.Hr
import flow.network.dto.topic.Image
import flow.network.dto.topic.ImageAligned
import flow.network.dto.topic.Italic
import flow.network.dto.topic.Link
import flow.network.dto.topic.PostBr
import flow.network.dto.topic.PostElementDto
import flow.network.dto.topic.Quote
import flow.network.dto.topic.Size
import flow.network.dto.topic.Spoiler
import flow.network.dto.topic.Text
import flow.network.dto.topic.TextAlignment
import flow.network.dto.topic.UList
import flow.network.dto.topic.Underscore
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.util.Locale

private typealias ElementsList = MutableList<PostElementDto>

internal object ParsePostUseCase {
    operator fun invoke(elements: Elements?): List<PostElementDto> {
        return mutableListOf<PostElementDto>().apply { elements?.let { appendElements(it) } }
    }

    private fun elementsList() = mutableListOf<PostElementDto>()

    private fun ElementsList.appendElements(elements: Elements) {
        elements.forEach { appendElement(it) }
    }

    private fun ElementsList.appendElement(element: Element?) {
        if (element != null) {
            if (element.childNodes().isNotEmpty()) {
                element.childNodes().forEach { appendNode(it) }
            } else if (element.text().isNotEmpty()) {
                text(element.text())
            }
        }
    }

    private fun ElementsList.appendNode(node: Node) {
        when (node) {
            is Element -> when {
                node.hasAttr("style") -> {
                    when (val style = node.getStyle()) {
                        is Style.Alignment -> align(style.alignment) { appendElement(node) }
                        is Style.Color -> color(style.color) { appendElement(node) }
                        is Style.Size -> size(style.size) { appendElement(node) }
                        null -> appendElement(node)
                    }
                }

                node.hasClass("ost-box") -> box { appendElement(node) }
                node.hasClass("post-b") -> bold { appendElement(node) }
                node.hasClass("post-i") -> italic { appendElement(node) }
                node.hasClass("post-u") -> underscore { appendElement(node) }
                node.hasClass("post-s") -> crossed { appendElement(node) }
                node.hasClass("postLink") -> link(node.url()) { appendElement(node) }
                node.hasClass("postImg") -> {
                    if (node.hasClass("postImgAligned")) {
                        imageAligned(
                            node.attr("title"),
                            when {
                                node.hasClass("img-left") -> Alignment.Start
                                node.hasClass("img-top") -> Alignment.Top
                                node.hasClass("img-right") -> Alignment.End
                                node.hasClass("img-bottom") -> Alignment.Bottom
                                else -> Alignment.Start
                            },
                        )
                    } else {
                        image(node.attr("title"))
                    }
                }

                node.hasClass("post-ul") -> uList { appendElement(node) }
                node.hasClass("c-wrap") -> code(
                    node.selectFirst(".c-head")?.text().orEmpty(),
                ) { appendElement(node.selectFirst(".c-body")) }

                node.hasClass("sp-wrap") -> spoiler(
                    node.selectFirst(".sp-head")?.text().orEmpty(),
                ) { appendElement(node.selectFirst(".sp-body")) }

                node.hasClass("q-wrap") -> quote(
                    node.selectFirst(".q-head")?.text().orEmpty(),
                    node.selectFirst(".q-post")?.text().orEmpty(),
                ) {
                    node.select(".q-post").remove()
                    appendElement(node.selectFirst(".q"))
                }

                node.hasClass("post-hr") || node.tag().name == "hr" -> hr()
                node.hasClass("post-br") -> postBr()
                node.tag().name == "br" -> br()
                else -> appendElement(node)
            }

            is TextNode -> text(node.wholeText)
        }
    }

    private fun ElementsList.text(value: String) {
        val textBlocks = value.split("\n").filter { it.isNotBlank() }
        textBlocks.forEachIndexed { index, text ->
            add(Text(text))
            if (index != textBlocks.lastIndex) {
                add(Br)
            }
        }
    }

    private fun ElementsList.align(alignment: TextAlignment, block: ElementsList.() -> Unit) {
        add(Align(alignment, elementsList().apply(block)))
    }

    private fun ElementsList.size(size: Int, block: ElementsList.() -> Unit) {
        add(Size(size, elementsList().apply(block)))
    }

    private fun ElementsList.color(color: ColorValue, block: ElementsList.() -> Unit) {
        add(Color(color, elementsList().apply(block)))
    }

    private fun ElementsList.bold(block: ElementsList.() -> Unit) {
        add(Bold(elementsList().apply(block)))
    }

    private fun ElementsList.italic(block: ElementsList.() -> Unit) {
        add(Italic(elementsList().apply(block)))
    }

    private fun ElementsList.underscore(block: ElementsList.() -> Unit) {
        add(Underscore(elementsList().apply(block)))
    }

    private fun ElementsList.crossed(block: ElementsList.() -> Unit) {
        add(Crossed(elementsList().apply(block)))
    }

    private fun ElementsList.box(block: ElementsList.() -> Unit) {
        add(Box(elementsList().apply(block)))
    }

    private fun ElementsList.uList(block: ElementsList.() -> Unit) {
        add(UList(elementsList().apply(block)))
    }

    private fun ElementsList.code(title: String, block: ElementsList.() -> Unit) {
        add(Code(title, elementsList().apply(block)))
    }

    private fun ElementsList.spoiler(title: String, block: ElementsList.() -> Unit) {
        add(Spoiler(title, elementsList().apply(block)))
    }

    private fun ElementsList.quote(title: String, id: String, block: ElementsList.() -> Unit) {
        add(Quote(title, id, elementsList().apply(block)))
    }

    private fun ElementsList.link(src: String, block: ElementsList.() -> Unit) {
        add(Link(src, elementsList().apply(block)))
    }

    private fun ElementsList.image(src: String) {
        add(Image(src))
    }

    private fun ElementsList.imageAligned(src: String, alignment: Alignment) {
        add(ImageAligned(src, alignment))
    }

    private fun ElementsList.hr() {
        add(Hr)
    }

    private fun ElementsList.br() {
        add(Br)
    }

    private fun ElementsList.postBr() {
        add(PostBr)
    }

    private fun Element?.getStyle(): Style? {
        return this?.runCatching {
            val styles = attr("style")
                .split(";")
                .filter(String::isNotBlank)
                .map { it.trim().split(":") }
                .filter { it.size > 1 }
                .associate { (key, value) -> key.trim() to value.trim() }
            when {
                styles.contains("text-align") -> {
                    Style.Alignment(
                        TextAlignment.valueOf(
                            styles.getValue("text-align").replaceFirstChar {
                                if (it.isLowerCase()) {
                                    it.titlecase(Locale.getDefault())
                                } else {
                                    it.toString()
                                }
                            },
                        ),
                    )
                }

                styles.contains("font-size") -> {
                    Style.Size(styles.getValue("font-size").filter(Char::isDigit).toInt())
                }

                styles.contains("color") -> {
                    val colorValue = styles.getValue("color")
                    Style.Color(
                        if (colorValue.startsWith("#")) {
                            ColorValue.Hex(colorValue.drop(1).toLong())
                        } else {
                            ColorValue.Name(colorValue)
                        },
                    )
                }

                else -> null
            }
        }?.getOrNull()
    }

    sealed interface Style {
        data class Alignment(val alignment: TextAlignment) : Style
        data class Size(val size: Int) : Style
        data class Color(val color: ColorValue) : Style
    }
}
