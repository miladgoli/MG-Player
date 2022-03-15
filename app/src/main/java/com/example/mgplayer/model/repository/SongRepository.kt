package com.example.mgplayer.model.repository

import com.example.mgplayer.model.Song
import io.reactivex.Completable
import io.reactivex.Single

interface SongRepository {

    fun setMusicsList(list: ArrayList<Song>): Completable

    fun deleteAllMusics(): Completable

    fun getAllMusics(): Single<List<Song>>
}