package me.rutrackersearch.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStorageFactoryImpl @Inject constructor(
    private val context: Context
) : SecureStorageFactory {

    private val mainKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    override fun getSharedPreferences(name: String): SharedPreferences {
        return EncryptedSharedPreferences.create(context, name, mainKey, AES256_SIV, AES256_GCM)
    }
}
