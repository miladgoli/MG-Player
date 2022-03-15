package com.example.mgplayer.model.repository

import com.example.mgplayer.model.Song
import com.example.mgplayer.model.database.SongDao
import io.reactivex.Completable
import io.reactivex.Single

class SongRepositoryImp(val dao: SongDao) : SongRepository {

    override fun setMusicsList(list: ArrayList<Song>): Completable {
        return dao.setMusicsList(list)
    }

    override fun deleteAllMusics(): Completable {
        return dao.deleteAllMusics()
    }

    override fun getAllMusics(): Single<List<Song>> {
        return dao.getAllMusics()
    }
}