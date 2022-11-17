package flow.securestorage.utils

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

@SuppressLint("CommitPrefEdits")
internal inline fun SharedPreferences.edit(block: Editor.() -> Editor) = edit().block().apply()

internal fun SharedPreferences.clear() = edit { clear() }
