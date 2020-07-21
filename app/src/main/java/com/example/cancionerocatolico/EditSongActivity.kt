package com.example.cancionerocatolico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.Song
import com.example.cancionerocatolico.utils.UserHelper
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_edit_song.*

/* Activity to CREATE or EDIT a SONG */
class EditSongActivity : AppCompatActivity() {
    var songID : Int = 0
    var cancAPI = CancioneroAPI({ UserHelper.getUserID(this) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_song)
        val btnCreateSong = findViewById<Button>(R.id.btnCreateSong)
        val btnUpdateSong = findViewById<Button>(R.id.btnUpdateSong)
        val tagsLayout = findViewById<FlexboxLayout>(R.id.tagsLayout)

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
            val id = 0
            val title = etxtSongTitle.text.toString()
            val artist = etxtSongArtist.text.toString()
            val lyrics = etxtSongLyrics.text.toString()
            val tags = etxtSongTags.text.toString()
            val newSong = Song(
                id,
                title,
                artist,
                lyrics,
                tags
            )
            addSongDB(newSong)
        }

        btnUpdateSong.setOnClickListener {
            val title = etxtSongTitle.text.toString()
            val artist = etxtSongArtist.text.toString()
            val lyrics = etxtSongLyrics.text.toString()
            val tags = etxtSongTags.text.toString()

            val currSong = Song(
                songID,
                title,
                artist,
                lyrics,
                tags
            )
            updateSongDB(currSong)
        }
        addNewChip("add", tagsLayout)
        addNewChip("close", tagsLayout)
    }

    fun addSongDB(song : Song){
        //Adds the song in DB
        cancAPI.addSong(song,
            success = { nSong ->
                val newSong  = nSong as Song
                songID = newSong.songID
                Toast.makeText(applicationContext, R.string.toast_createSong, Toast.LENGTH_SHORT).show()
                finish()
            })
        //TODO: Create a fail behaviour?
    }

    fun updateSongDB(song : Song){
        //Updates the song in DB
        cancAPI.updateSong(song,
            success = {
                Toast.makeText(applicationContext, R.string.toast_updateSong, Toast.LENGTH_SHORT).show()
                finish()
            })
        //TODO: Create a fail behaviour?
    }

    fun addNewChip(tag: String, chipGroup: FlexboxLayout){
        val chip = layoutInflater.inflate(R.layout.chip_elem, chipGroup, false) as Chip
        chip.text = tag
        chip.isCloseIconVisible = true
        chip.isClickable = true
        chipGroup.addView(chip as View, chipGroup.childCount -1)
        chip.setOnCloseIconClickListener { chipGroup.removeView(chip as View) }
    }


}