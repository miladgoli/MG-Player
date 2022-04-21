package com.example.mgplayer

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mgplayer.databinding.ActivityPleyerBinding
import com.example.mgplayer.model.Music.convertMillisToString
import com.example.mgplayer.model.Song
import com.example.mgplayer.view.home.HomeAdapter
import com.example.mgplayer.viewmodel.SongViewModel
import com.example.mgplayer.viewmodel.SongViewModelProvider
import java.lang.String
import java.util.*
import kotlin.collections.ArrayList

class PleyerActivity : AppCompatActivity(), HomeAdapter.OnSongClickListener {

    lateinit var binding: ActivityPleyerBinding

    private val TAG = "PlayerActivity"
    var checkFavorite: Boolean = false
    var checkPlayMusic: Boolean = true
    var idRepeat: Int = 1
    var nowPlaying: Int = 0
    var music: Song? = null
    var musicList: ArrayList<Song> = ArrayList()
    lateinit var timer: Timer
    lateinit var viewModel: SongViewModel


    var musicPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPleyerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        timer = Timer()

        initializeAndCheckViewModel()

        music = intent.extras?.getParcelable("music")
        nowPlaying = intent.getIntExtra("position", 0)
        musicList = intent.getSerializableExtra("listMusic") as ArrayList<Song>



        viewModel.getAllMusics()
        viewModel.getMusics().observe(this, androidx.lifecycle.Observer {
            musicList = it as ArrayList<Song>
        })
        musicPlayer = MediaPlayer.create(this, Uri.parse(musicList.get(nowPlaying).path))
        musicPlayer?.start()

        setInfoMusic(nowPlaying, true)

        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    musicPlayer?.let {
                        binding.textViewNowDuration.text =
                            convertMillisToString(it.currentPosition.toLong()).toString()
                        binding.seekBarPlay.progress = it.currentPosition
                    }
                }
            }
        }, 0, 1000)

        binding.seekBarPlay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (idRepeat == 3 && checkPlayMusic == false) {
                    setInfoMusic(nowPlaying, true)
                    binding.btnPausePlay.icon = getDrawable(R.drawable.ic_pause)
                    checkPlayMusic = true
                }
                musicPlayer?.let {
                    it.seekTo(seekBar!!.progress)
                    binding.textViewNowDuration.text =
                        convertMillisToString(seekBar.progress.toLong())
                }

            }

        })
        musicPlayer?.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
            override fun onCompletion(mp: MediaPlayer?) {
                try {
                    if (idRepeat == 1) {
                        nextMusic()
                    } else if (idRepeat == 2) {
                        repeatMusic()
                    } else if (idRepeat == 3) {
                        binding.btnPausePlay.icon = getDrawable(R.drawable.ic_resume)
                        stopMusic()
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })


        //Buttons Click Listener
        reactionCenterButtons()
        reactionBottomButtons()
    }

    fun stopMusic() {
        musicPlayer?.let {
            checkPlayMusic = false
            it.reset()
            it.prepare()
            it.seekTo(0)
        }
    }

    fun reactionCenterButtons() {


        //Pause/Play Button
        binding.btnPausePlay.setOnClickListener {
            if (idRepeat == 3 && checkPlayMusic == false) {
                setInfoMusic(nowPlaying, true)
            }
            if (checkPlayMusic == false) {

                musicPlayer?.start()
                binding.btnPausePlay.icon = getDrawable(R.drawable.ic_pause)
            } else {

                musicPlayer?.pause()
                binding.btnPausePlay.icon = getDrawable(R.drawable.ic_resume)
            }
            checkPlayMusic = !checkPlayMusic
        }

        binding.btnNextMusic.setOnClickListener {
            nextMusic()
        }

        binding.btnBackMusic.setOnClickListener {

            nowPlaying = nowPlaying - 1
            if (nowPlaying < 0) {
                nowPlaying = musicList.size - 1
            }

            musicPlayer?.let {
                if (it.isPlaying) {
                    setInfoMusic(nowPlaying, true)
                } else {
                    setInfoMusic(nowPlaying, true)
                    checkPlayMusic = true
                    binding.btnPausePlay.icon = getDrawable(R.drawable.ic_pause)
                }
            }

        }


    }

    fun reactionBottomButtons() {
        binding.btnRepeatMusic.setOnClickListener {

            if (idRepeat == 1) {
                binding.btnRepeatMusic.setImageResource(R.drawable.ic_loop_one)
                idRepeat = 2
            } else if (idRepeat == 2) {
                binding.btnRepeatMusic.setImageResource(R.drawable.ic_loop)
                binding.btnRepeatMusic.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.app_purple_hint
                    )
                )
                idRepeat = 3
            } else if (idRepeat == 3) {
                binding.btnRepeatMusic.setColorFilter( ContextCompat.getColor(
                    this,
                    R.color.app_purple_dark
                ))
                binding.btnRepeatMusic.setImageResource(R.drawable.ic_loop)
                idRepeat = 1
            }
        }

        binding.btnAddToFavorite.setOnClickListener {
            Toast.makeText(this,"Coming soon !",Toast.LENGTH_SHORT).show()
        }

        binding.btnShare.setOnClickListener {
            Toast.makeText(this,"Coming soon !",Toast.LENGTH_SHORT).show()
        }

    }

    fun setInfoMusic(nowPlay: Int, start: Boolean) {
        musicPlayer?.let {
            it.reset()
            var music: Song = musicList.get(nowPlay)
            val path = Uri.parse(music.path)
            it.setDataSource(String.valueOf(path))
            it.prepare()
            it.seekTo(0)
            if (start) {
                it.start()
            }

            binding.seekBarPlay.max = it.duration

            binding.textViewDuration.text =
                convertMillisToString(it.duration.toLong()).toString()
            binding.musicName.text = music.name

            val albumArt: kotlin.String? = music.albumArt
            if (albumArt != null) {
                binding.imgMusicPlayer.setImageURI(Uri.parse(albumArt))

                if (binding.imgMusicPlayer.drawable == null) {
                    binding.imgMusicPlayer.setImageResource(R.drawable.man)
                }
            } else {
                binding.imgMusicPlayer.setImageResource(R.drawable.man)
            }

            binding.artistName.text = music.artist

        }
    }

    fun nextMusic() {
        nowPlaying++
        if (nowPlaying == musicList.size) {
            nowPlaying = 0
        }

        musicPlayer?.let {
            if (it.isPlaying) {
                setInfoMusic(nowPlaying, true)
            } else {
                setInfoMusic(nowPlaying, true)
                binding.btnPausePlay.icon = getDrawable(R.drawable.ic_pause)
            }
        }
    }

    override fun onDestroy() {
        timer.cancel()
        musicPlayer?.release()
        musicPlayer = null
        super.onDestroy()
    }

    fun repeatMusic() {
        if (nowPlaying == musicList.size) {
            nowPlaying = 0
        }
        setInfoMusic(nowPlaying, true)

    }

    override fun onSongClick(song: Song, position: Int, list: ArrayList<Song>) {

    }

    fun initializeAndCheckViewModel() {
        //initialize view model
        viewModel = ViewModelProvider(
            this,
            SongViewModelProvider(this)
        ).get(SongViewModel::class.java)

    }
}