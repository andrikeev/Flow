package me.rutrackersearch.data.utils

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

@SuppressLint("CommitPrefEdits")
inline fun SharedPreferences.edit(block: Editor.() -> Editor) = edit().block().apply()

fun SharedPreferences.clear() = edit { clear() }
