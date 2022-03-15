package com.example.mgplayer.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mgplayer.model.Song

@Database(
    entities = [Song::class],
    version = 1,
    exportSchema = false
)
abstract class SongDatabase : RoomDatabase() {

    abstract fun getMusicDao(): SongDao
}