@file:Suppress("unused")

package me.rutrackersearch.app.ui.parcel

import android.os.Parcel
import kotlinx.parcelize.Parceler
import java.util.EnumSet

fun Parcel.requireString(): String {
    return checkNotNull(readString()) { "required string value is null or missing" }
}

fun <T> Parcel.write(value: T, parceler: Parceler<T>, flags: Int) {
    val parcel = this
    with(parceler) {
        value.write(parcel, flags)
    }
}

fun <T> Parcel.read(parceler: Parceler<T>): T {
    return parceler.create(this)
}

fun <T> Parcel.writeList(items: List<T>?, parceler: Parceler<T>, flags: Int) {
    if (items == null) {
        writeInt(-1)
    } else {
        writeInt(items.size)
        val parcel = this
        with(parceler) {
            items.forEach { item ->
                item.write(parcel, flags)
            }
        }
    }
}

fun <T> Parcel.readList(parceler: Parceler<T>): List<T>? {
    return when (val size = readInt()) {
        -1 -> null
        0 -> emptyList()
        1 -> listOf(parceler.create(this))
        else -> {
            val result = ArrayList<T>(size)
            for (i in 0 until size) {
                result.add(parceler.create(this))
            }
            result
        }
    }
}

fun <T> Parcel.requireList(parceler: Parceler<T>): List<T> {
    return checkNotNull(readList(parceler)) { "required list value is null or missing" }
}

fun <T : Enum<T>> Parcel.writeEnum(value: T?) {
    if (value == null) {
        writeInt(-1)
    } else {
        writeInt(value.ordinal)
    }
}

fun Parcel.writeBytes(b: ByteArray?) {
    if (b != null) {
        writeInt(b.size)
        writeByteArray(b)
    } else {
        writeInt(-1)
    }
}

fun Parcel.readBytes(): ByteArray? {
    val len = readInt()
    return if (len != -1) {
        val bytes = ByteArray(len)
        readByteArray(bytes)
        bytes
    } else {
        null
    }
}

inline fun <reified T : Enum<T>> Parcel.readEnum(): T? {
    val ordinal = readInt()
    return if (ordinal != -1) {
        enumValues<T>()[ordinal]
    } else {
        null
    }
}

inline fun <reified T : Enum<T>> Parcel.requireEnum(): T {
    return checkNotNull(readEnum<T>()) { "required enum value is null or missing" }
}

fun <T : Enum<T>> Parcel.writeEnums(items: Set<T>?) {
    if (items == null) {
        writeInt(-1)
    } else {
        writeInt(items.size)
        items.forEach { item ->
            writeEnum(item)
        }
    }
}

inline fun <reified T : Enum<T>> Parcel.readEnums(): Set<T>? {
    return when (val size = readInt()) {
        -1 -> null
        0 -> emptySet()
        1 -> setOf(requireEnum())
        else -> {
            val result = EnumSet.noneOf(T::class.java)
            for (i in 0 until size) {
                result.add(requireEnum())
            }
            result
        }
    }
}

inline fun <reified T : Enum<T>> Parcel.requireEnums(): Set<T> {
    return checkNotNull(readEnums()) { "required enums value is null or missing" }
}

fun <K, V> Parcel.writeMap(
    map: Map<K, V>?,
    keyWriter: (K, Parcel, Int) -> Unit,
    valueWriter: (V, Parcel, Int) -> Unit,
    flags: Int
) {
    if (map == null) {
        writeInt(-1)
    } else {
        writeInt(map.size)
        val parcel = this
        map.forEach { (key, value) ->
            keyWriter.invoke(key, parcel, flags)
            valueWriter.invoke(value, parcel, flags)
        }
    }
}

fun <K, V> Parcel.readMap(
    keyReader: (Parcel) -> K,
    valueReader: (Parcel) -> V
): Map<K, V>? {
    return when (val size = readInt()) {
        -1 -> null
        0 -> emptyMap()
        1 -> mapOf(keyReader.invoke(this) to valueReader.invoke(this))
        else -> {
            val result = HashMap<K, V>(size)
            for (i in 0 until size) {
                result[keyReader.invoke(this)] = valueReader.invoke(this)
            }
            result
        }
    }
}

fun <K, V> Parcel.requireMap(
    keyReader: (Parcel) -> K,
    valueReader: (Parcel) -> V
): Map<K, V> {
    return checkNotNull(readMap(keyReader, valueReader)) {
        "required map value is null or missing"
    }
}
