package com.example.mgplayer.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mgplayer.model.Song
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface SongDao {

    @Insert
    fun setMusicsList(list: ArrayList<Song>): Completable

    @Query("Delete from tbl_music")
    fun deleteAllMusics(): Completable

    @Query("select * from tbl_music")
    fun getAllMusics(): Single<List<Song>>
}