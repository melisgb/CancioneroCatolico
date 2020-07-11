package com.example.cancionerocatolico

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit_song.*
import java.net.URLEncoder
import kotlin.random.Random
/* Activity to CREATE or EDIT a SONG */
class EditSongActivity : AppCompatActivity() {
    var songID : Int = 0
    var cancAPI = CancioneroAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_song)
        val btnCreateSong = findViewById<Button>(R.id.btnCreateSong)
        val btnUpdateSong = findViewById<Button>(R.id.btnUpdateSong)

        if(intent.extras != null){
            txtvEditSongTitle.text = "Update Song"
            btnUpdateSong.isEnabled = true
            btnCreateSong.isEnabled = false
            //Para Editar Cancion
            val bundle = intent.extras!!
            songID = bundle.getInt("songId")
            etxtSongTitle.setText(bundle.getString("songTitle"))
            etxtSongArtist.setText(bundle.getString("songArtist"))
            etxtSongLyrics.setText(bundle.getString("songLyrics"))
            etxtSongTags.setText(bundle.getString("songTags"))
        }
        else{
            txtvEditSongTitle.text = "Create Song"
            btnUpdateSong.isEnabled = false
            btnCreateSong.isEnabled = true
        }

        btnCreateSong.setOnClickListener {
            val myToast = Toast.makeText(applicationContext, R.string.toast_createSong, Toast.LENGTH_SHORT)
            myToast.setGravity(Gravity.BOTTOM, 0, 50)
            myToast.show()
            //TODO: Create song in DB
            val id = 0
            val title = etxtSongTitle.text.toString()
            val artist = etxtSongArtist.text.toString()
            val lyrics = etxtSongLyrics.text.toString()
            val tags = etxtSongTags.text.toString()

            val newSong = Song(id, title, artist, lyrics, tags)
            //TODO: add the song into the list / DB
            addSongDB(newSong)
            finish()
        }

        btnUpdateSong.setOnClickListener {
            val title = etxtSongTitle.text.toString()
            val artist = etxtSongArtist.text.toString()
            val lyrics = etxtSongLyrics.text.toString()
            val tags = etxtSongTags.text.toString()

            val currSong = Song(songID, title, artist, lyrics, tags)
            updateSongDB(currSong)
        }
    }

    fun addSongDB(song : Song){
        //Adds the song in DB


        cancAPI.addSong(song,
            success = { nSong ->
                val newSong  = nSong as Song
                songID = newSong.songID
                Toast.makeText(applicationContext, "Song added", Toast.LENGTH_SHORT).show()
                finish()
            })
        //TODO: Create a fail behaviour?
    }

    fun updateSongDB(song : Song){
        //Updates the song in DB
        cancAPI.updateSong(song,
            success = {
                Toast.makeText(applicationContext, "Song updated", Toast.LENGTH_SHORT).show()
                finish()
            })
        //TODO: Create a fail behaviour?
    }
}