package me.rutrackersearch.data.security

import android.content.SharedPreferences

interface SecureStorageFactory {
    fun getSharedPreferences(name: String): SharedPreferences
}
