package me.rutrackersearch.data.download

import android.text.TextUtils
import java.nio.charset.StandardCharsets

internal fun buildValidFatFilename(name: String): String {
    if (TextUtils.isEmpty(name) || "." == name || ".." == name) {
        return "(invalid)"
    }
    val res = StringBuilder(name.length)
    for (element in name) {
        if (isValidFatFilenameChar(element)) {
            res.append(element)
        } else {
            res.append('_')
        }
    }
    trimFilename(res)
    return res.toString()
}

private fun isValidFatFilenameChar(c: Char): Boolean {
    return if (c.code in 0x00..0x1f || c.code == 0x7F) {
        false
    } else when (c) {
        '"', '*', '/', ':', '<', '>', '?', '\\', '|' -> false
        else -> true
    }
}

private const val maxBytes = 255

private fun trimFilename(res: StringBuilder) {
    var maxBytesNumber = maxBytes
    var raw = res.toString().toByteArray(StandardCharsets.UTF_8)
    if (raw.size > maxBytesNumber) {
        maxBytesNumber -= 3
        while (raw.size > maxBytesNumber) {
            res.deleteCharAt(res.length / 2)
            raw = res.toString().toByteArray(StandardCharsets.UTF_8)
        }
        res.insert(res.length / 2, "...")
    }
}
