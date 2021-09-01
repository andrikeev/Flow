package me.rutrackersearch.data.database

import android.content.Context
import androidx.room.Room
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseFactoryImpl @Inject constructor(
    private val context: Context,
) : DatabaseFactory {
    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "flow-db").build()
    }

    override fun get(): AppDatabase = appDatabase
}
