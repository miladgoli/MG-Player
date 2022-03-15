package com.example.mgplayer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.mgplayer.model.Utils.DATABASE_NAME_MUSIC
import com.example.mgplayer.model.database.SongDao
import com.example.mgplayer.model.database.SongDatabase
import com.example.mgplayer.model.repository.SongRepository
import com.example.mgplayer.model.repository.SongRepositoryImp

class SongViewModelProvider(val context: Context) : ViewModelProvider.Factory {

    lateinit var dao: SongDao
    lateinit var songRepository: SongRepository
    lateinit var database: SongDatabase

    fun initializeDatabase() {
        database =
            Room.databaseBuilder(context, SongDatabase::class.java, DATABASE_NAME_MUSIC).build()
        dao = database.getMusicDao()
        songRepository = SongRepositoryImp(dao)
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        initializeDatabase()
        return SongViewModel(songRepository) as T
    }
}