package me.rutrackersearch.data.database

interface DatabaseFactory {
    fun get(): AppDatabase
}
