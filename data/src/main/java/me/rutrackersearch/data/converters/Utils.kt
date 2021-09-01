package me.rutrackersearch.data.converters

import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

fun ResponseBody.readJson(): JSONObject {
    return use {
        JSONObject(string())
    }
}

fun JSONObject.optionalString(key: String): String? {
    return if (has(key)) {
        getString(key)
    } else {
        null
    }
}

fun JSONObject.optionalLong(key: String): Long? {
    return if (has(key)) {
        getLong(key)
    } else {
        null
    }
}

fun JSONObject.optionalInt(key: String): Int? {
    return if (has(key)) {
        getInt(key)
    } else {
        null
    }
}

inline fun <reified T : Enum<T>> JSONObject.optionalEnum(key: String): T? {
    return if (has(key)) {
        enumValueOf<T>(getString(key))
    } else {
        null
    }
}

fun JSONArray.parseStringList(): List<String> {
    return IntRange(0, length() - 1).map { index ->
        getString(index)
    }
}

inline fun <reified T> JSONArray.parseList(parser: JSONObject.() -> T): List<T> {
    return IntRange(0, length() - 1).map { index ->
        getJSONObject(index).parser()
    }
}