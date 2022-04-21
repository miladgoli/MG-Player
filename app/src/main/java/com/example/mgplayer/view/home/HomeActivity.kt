package com.example.mgplayer.view.home


import android.app.AlertDialog
import android.content.ContentUris
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mgplayer.databinding.ActivityHomeBinding
import com.example.mgplayer.model.Song

import com.example.mgplayer.PleyerActivity
import com.example.mgplayer.viewmodel.SongViewModel
import com.example.mgplayer.viewmodel.SongViewModelProvider


class HomeActivity : AppCompatActivity(), HomeAdapter.OnSongClickListener {

    //initialize variables
    lateinit var binding: ActivityHomeBinding
    lateinit var songLibraryUri: Uri
    private val TAG = "HomeActivity"
    var songsList: ArrayList<Song> = ArrayList()
    lateinit var storagePermissionLauncher: ActivityResultLauncher<String>
    lateinit var adapter: HomeAdapter
    lateinit var viewModel: SongViewModel


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reactionButtons()

        //initialize view model
        initializeAndCheckViewModel()

        //check permission
        if (checkReadStoragePermissions()) {
            songsList = fetchSongs()
        }

        viewModel.setMusicsList(songsList)

        //setup recycler view
        setupRecyclerView()
    }

    private fun reactionButtons() {

        binding.btnFavoriteList.setOnClickListener {
            Toast.makeText(this, "Coming soon !", Toast.LENGTH_SHORT).show()
        }

        binding.btnAppInfo.setOnClickListener {
            Toast.makeText(this, "Coming soon !", Toast.LENGTH_SHORT).show()
        }

        binding.btnShuffle.setOnClickListener {
            Toast.makeText(this, "Coming soon !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {

        adapter = HomeAdapter(this)
        if (songsList.size > 0) {
            adapter.setSongsListAdapter(songsList)
            binding.tvCheckNullMusic.text = ""
        } else {
            binding.tvCheckNullMusic.text = "Not Find Music!"
        }


        binding.recyclerviewHome.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.recyclerviewHome.adapter = adapter

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun respondOnUserPermissionActs(): Boolean {
        //user response
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //permission granted
            return true
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //show dialog don't allowed permission
            //show dialog
            AlertDialog.Builder(this)
                .setTitle("Get Permission")
                .setMessage("Allow us to fetch & show songs on your device")
                .setPositiveButton("Allow", DialogInterface.OnClickListener { dialog, which ->
                    //request permission again
                    storagePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                })
                .setNegativeButton("Deny", DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(this, "You denied to fetch songs", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                })
                .show()
            return false
        } else {
            Toast.makeText(this, "You denied to fetch songs", Toast.LENGTH_LONG).show()
            return false
        }

    }

    private fun fetchSongs(): ArrayList<Song> {
        //define list to carry the songs
        val newList = ArrayList<Song>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            songLibraryUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

        } else {

            songLibraryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        }
        //projection
        var projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA
        )

        //sort order
        val sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC"

        //Querying
        contentResolver.query(songLibraryUri, projection, null, null, sortOrder).use { cursor ->

            //cache the cursor indices
            val idColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val artistColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val albumIdColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val pathColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)


            //getting the values
            while (cursor?.moveToNext()!!) {

                //get values of columns for a give audio file
                val id = cursor.getLong(idColumn!!)
                var name = cursor.getString(nameColumn!!)
                val artist = cursor.getString(artistColumn!!)
                val duration = cursor.getInt(durationColumn!!)
                val size = cursor.getInt(sizeColumn!!)
                val albumId = cursor.getLong(albumIdColumn!!)
                val path = cursor.getString(pathColumn!!)

                //song uri
                val uri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                //album art uri
                val albumartUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )

                //remove .mp3 on songs name
                name = name.substring(0, name.lastIndexOf("."))

                //song item
                val song = Song()
                song.id = id
                song.name = name
                song.artist = artist
                song.duration = duration
                song.size = size
                song.albumId = albumId
                song.albumArt = albumartUri.toString()
                song.isFavorite = false
                song.path = path
                //add song to songs list
                newList.add(song)
            }

        }
        return newList
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkReadStoragePermissions(): Boolean {

        var checkPermission = true

        storagePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //permission was granted
                    checkPermission = true
                } else {
                    //responding on user's actions
                    respondOnUserPermissionActs()
                    checkPermission = false
                }
            }

        storagePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return checkPermission
    }

    override fun onSongClick(song: Song, position: Int, list: ArrayList<Song>) {
        val intent = Intent(this, PleyerActivity::class.java)
        intent.putExtra("music", song)
        intent.putExtra("position", position)
        intent.putExtra("listMusic", list)
        startActivity(intent)
    }

    fun initializeAndCheckViewModel() {
        //initialize view model
        viewModel = ViewModelProvider(
            this,
            SongViewModelProvider(this)
        ).get(SongViewModel::class.java)

    }
}