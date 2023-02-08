package flow.network.domain

import flow.network.dto.forum.CategoryDto
import flow.network.dto.topic.AuthorDto
import flow.network.dto.topic.CommentsPageDto
import flow.network.dto.topic.PostDto
import org.jsoup.Jsoup

internal object ParseCommentsPageUseCase {
    operator fun invoke(html: String): CommentsPageDto {
        val doc = Jsoup.parse(html)
        val id = requireIdFromUrl(doc.select("#topic-title").url(), "t")
        val title = doc.select("#topic-title").toStr()
        val categoryNode = doc.select(".nav.w100.pad_2").select("a")
        val categoryId = requireIdFromUrl(categoryNode.last().url(), "f")
        val categoryName = categoryNode.last().toStr()

        val firstPost = doc.select("tbody[id^=post]").first()
        if (!firstPost?.select(".magnet-link").urlOrNull().isNullOrEmpty()) {
            firstPost?.remove()
        }

        val navigation = doc.select("#pagination > tbody > tr > td > p:nth-child(1)")
        val currentPage = navigation.select("b:nth-child(1)").toInt(1)
        val totalPages = navigation.select("b:nth-child(2)").toInt(1)
        val posts = doc.select("tbody[id^=post]")

        val topicPosts = posts.map { post ->
            val postId = post.select(".post_body").attr("id").substringAfter("p-")
            val author = AuthorDto(
                id = getIdFromUrl(post.select(".poster_btn").select(".txtb").first().urlOrNull(), "u"),
                name = post.select(".nick").text(),
                avatarUrl = post.select(".avatar > img").attr("src")
            )
            val date = post.select(".p-link").text()
            PostDto(postId, author, date, ParsePostUseCase(post.select(".post_body")))
        }

        return CommentsPageDto(
            id = id,
            title = title,
            category = CategoryDto(categoryId, categoryName),
            page = currentPage,
            pages = totalPages,
            posts = topicPosts
        )
    }
}
