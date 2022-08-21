package me.rutrackersearch.data.database.converters

import androidx.room.TypeConverter
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Order
import me.rutrackersearch.models.search.Period
import me.rutrackersearch.models.search.Sort
import me.rutrackersearch.models.topic.Author
import org.json.JSONArray
import org.json.JSONObject

object Converters {

    @TypeConverter
    fun toSort(value: String) = enumValueOf<Sort>(value)

    @TypeConverter
    fun fromSort(value: Sort) = value.name

    @TypeConverter
    fun toOrder(value: String) = enumValueOf<Order>(value)

    @TypeConverter
    fun fromOrder(value: Order) = value.name

    @TypeConverter
    fun toPeriod(value: String) = enumValueOf<Period>(value)

    @TypeConverter
    fun fromPeriod(value: Period) = value.name

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return JSONArray(value).parseStringList()
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return JSONArray().apply {
            value.forEach { put(it) }
        }.toString()
    }

    @TypeConverter
    fun toAuthor(value: String?): Author? {
        if (value == null) return null
        return JSONObject(value).parseAuthor()
    }

    @TypeConverter
    fun fromAuthor(value: Author?): String? {
        if (value == null) return null
        return JSONObject().apply {
            value.id?.let { put("id", it) }
            put("name", value.name)
            value.avatarUrl?.let { put("avatarUrl", it) }
        }.toString()
    }

    @TypeConverter
    fun toCategory(value: String?): Category? {
        if (value == null) return null
        return JSONObject(value).parseCategory()
    }

    @TypeConverter
    fun fromCategory(value: Category?): String? {
        if (value == null) return null
        return JSONObject().apply {
            put("id", value.id)
            put("name", value.name)
        }.toString()
    }

    @TypeConverter
    fun toCategories(value: String?): List<Category>? {
        if (value == null) return null
        return JSONArray(value).parseList { parseCategory() }
    }

    @TypeConverter
    fun fromCategories(value: List<Category>?): String? {
        if (value == null) return null
        return JSONArray().apply {
            value.forEach { category ->
                put(
                    JSONObject().apply {
                        put("id", category.id)
                        put("name", category.name)
                    }
                )
            }
        }.toString()
    }

    private fun JSONObject.parseCategory(): Category {
        return Category(
            id = getString("id"),
            name = getString("name"),
        )
    }

    private fun JSONObject.parseAuthor(): Author {
        return Author(
            id = optionalString("id"),
            name = getString("name"),
            avatarUrl = optionalString("avatarUrl"),
        )
    }

    private fun JSONObject.optionalString(key: String): String? {
        return if (has(key)) {
            getString(key)
        } else {
            null
        }
    }

    private fun JSONArray.parseStringList(): List<String> {
        return IntRange(0, length() - 1).map { index ->
            getString(index)
        }
    }

    private inline fun <reified T> JSONArray.parseList(parser: JSONObject.() -> T): List<T> {
        return IntRange(0, length() - 1).map { index ->
            getJSONObject(index).parser()
        }
    }
}
