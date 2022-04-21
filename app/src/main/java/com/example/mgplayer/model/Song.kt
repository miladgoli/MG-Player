package com.example.mgplayer.model

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mgplayer.model.Utils.TABLE_NAME_MUSICS
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = TABLE_NAME_MUSICS)
data class Song(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String? = null,
    var artist:String?=null,
    var duration: Int? = null,
    var size: Int? = null,

    var albumId: Long = 0,
    var path:String?=null,
    var albumArt: String? = null,
    var isFavorite: Boolean = false

) : Parcelable
