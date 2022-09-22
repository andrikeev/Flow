package me.rutrackersearch.data.database

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseFactoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : DatabaseFactory {
    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "flow-db").build()
    }

    override fun get(): AppDatabase = appDatabase
}
