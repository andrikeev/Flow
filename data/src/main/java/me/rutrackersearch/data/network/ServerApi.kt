package me.rutrackersearch.data.network

import me.rutrackersearch.domain.entity.search.Order
import me.rutrackersearch.domain.entity.search.Period
import me.rutrackersearch.domain.entity.search.Sort
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

const val BASE_URL = "https://flow.rutrackersearch.me/v3/"

interface ServerApi {
    @GET("search")
    suspend fun search(
        @Query("query") query: String,
        @Query("sort") sort: Sort,
        @Query("order") order: Order,
        @Query("period") period: Period,
        @Query("author") author: String,
        @Query("authorId") authorId: String,
        @Query("categories") categories: String,
        @Query("page") page: Int,
    ): ResponseBody

    @GET("forumtree")
    suspend fun forumTree(): ResponseBody

    @GET("category")
    suspend fun category(
        @Query("id") id: String,
        @Query("page") page: Int,
    ): ResponseBody

    @GET("favorites")
    suspend fun favorites(@Query("page") page: Int): ResponseBody

    @POST("favorites/add")
    suspend fun addFavorite(@Query("id") id: String): ResponseBody

    @POST("favorites/remove")
    suspend fun removeFavorite(@Query("id") id: String): ResponseBody

    @GET("topic")
    suspend fun topic(@Query("id") id: String, @Query("pid") pid: String): ResponseBody

    @GET("comments")
    suspend fun comments(
        @Query("id") id: String,
        @Query("page") page: Int,
    ): ResponseBody

    @POST("comments/add")
    suspend fun addComment(
        @Query("topicId") topicId: String,
        @Query("message") message: String,
    ): ResponseBody

    @GET("torrent")
    suspend fun torrent(@Query("id") id: String): ResponseBody
}
