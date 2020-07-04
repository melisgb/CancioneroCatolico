package com.example.cancionerocatolico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit_song.*
import kotlin.random.Random

class EditSongActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_song)

        btnUpdateSong.isEnabled = false

        val btnCreateSong = findViewById<Button>(R.id.btnCreateSong)
        btnCreateSong.setOnClickListener {
            val myToast = Toast.makeText(applicationContext, R.string.toast_createSong, Toast.LENGTH_SHORT)
            myToast.setGravity(Gravity.BOTTOM, 0, 50)
            myToast.show()
            //TODO: Create song in DB
            val id = Random.nextInt()
            val title = etxtSongTitle.text.toString()
            val artist = etxtSongArtist.text.toString()
            val lyrics = etxtSongLyrics.text.toString()
            val tags = etxtSongTags.text.toString()

            val newSong = Song(id, title, artist, lyrics, tags)
            //TODO: add the song into the list
            finish()
        }



    }
}