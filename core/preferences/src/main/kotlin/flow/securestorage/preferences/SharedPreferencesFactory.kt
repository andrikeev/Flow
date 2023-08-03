package flow.securestorage.preferences

import android.content.SharedPreferences

internal interface SharedPreferencesFactory {
    fun getSharedPreferences(name: String): SharedPreferences
}
