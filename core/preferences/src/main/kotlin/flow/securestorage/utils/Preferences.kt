package flow.securestorage.utils

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

internal inline fun SharedPreferences.edit(block: Editor.() -> Editor) = edit().block().apply()

internal fun SharedPreferences.clear() = edit { clear() }
