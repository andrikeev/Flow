package flow.securestorage.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SharedPreferencesFactoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SharedPreferencesFactory {

    private val mainKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    override fun getSharedPreferences(name: String): SharedPreferences {
        return runCatching {
            EncryptedSharedPreferences.create(context, name, mainKey, AES256_SIV, AES256_GCM)
        }.getOrElse {
            context.getSharedPreferences(name, MODE_PRIVATE)
        }
    }
}
