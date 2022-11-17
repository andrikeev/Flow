package flow.securestorage.preferences

import android.content.SharedPreferences

internal interface SecurePreferencesFactory {
    fun getSharedPreferences(name: String): SharedPreferences
}
