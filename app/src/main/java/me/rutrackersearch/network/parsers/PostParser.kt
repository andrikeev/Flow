package me.rutrackersearch.network.parsers

import me.rutrackersearch.models.topic.Alignment
import me.rutrackersearch.models.topic.Content
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
import me.rutrackersearch.network.utils.url
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.parser.Tag
import org.jsoup.select.Elements

fun parseContent(post: Element): Content = elementsList().let {
    it.appendElements(post.select(".post_body"))
    it.parseContent()
}

private typealias ElementsList = MutableList<PostElement>

private fun elementsList() = mutableListOf<PostElement>()

private fun ElementsList.appendElements(elements: Elements) {
    elements.forEach(this::appendElement)
}

private fun ElementsList.appendElement(element: Element) {
    if (element.childNodes().isNotEmpty()) {
        element.childNodes().forEach(this::appendNode)
    } else if (element.text().isNotEmpty()) {
        text(element.text())
    }
}

private fun ElementsList.appendNode(node: Node) {
    when (node) {
        is Element -> when {
            node.className().contains("post-font") -> appendElement(node)
            node.hasClass("ost-box") -> box { appendElement(node) }
            node.hasClass("post-b") -> bold { appendElement(node) }
            node.hasClass("post-i") -> italic { appendElement(node) }
            node.hasClass("post-u") -> underscore { appendElement(node) }
            node.hasClass("post-s") -> crossed { appendElement(node) }
            node.hasClass("postLink") -> link(node.url()) { appendElement(node) }
            node.hasClass("postImg") && !node.hasClass("postImgAligned") -> image(node.attr("title"))
            node.hasClass("postImg") && node.hasClass("postImgAligned") -> imageAligned(
                node.attr("title"), when {
                    node.hasClass("img-left") -> Alignment.start
                    node.hasClass("img-top") -> Alignment.top
                    node.hasClass("img-right") -> Alignment.end
                    node.hasClass("img-bottom") -> Alignment.bottom
                    else -> Alignment.start
                }
            )
            node.hasClass("post-ul") -> uList { appendElement(node) }
            node.hasClass("c-wrap") -> code(node.select(".c-head").text()) { appendElements(node.select(".c-body")) }
            node.hasClass("sp-wrap") -> spoiler(
                node.select(".sp-head").text()
            ) { appendElements(node.select(".sp-body")) }
            node.hasClass("q-wrap") -> quote(node.select(".q-head").text(), node.select(".q-post").text()) {
                node.select(".q-post").remove()
                appendElements(node.select(".q"))
            }
            node.hasClass("post-hr") -> hr()
            node.hasClass("post-br") || node.tag() == Tag.valueOf("br") -> br()
            else -> appendElement(node)
        }
        is TextNode -> text(node.text())
    }
}

private fun ElementsList.text(value: String) {
    if (value.isNotBlank()) {
        add(Text(value.replace("\n", "").replace("\t", "").trim()))
    }
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