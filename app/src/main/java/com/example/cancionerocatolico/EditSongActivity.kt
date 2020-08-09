package com.example.cancionerocatolico

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.Song
import com.example.cancionerocatolico.utils.UserHelper
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_edit_song.*

/* Activity to CREATE or EDIT a SONG */
class EditSongActivity : AppCompatActivity() {
    private var songID : Int = 0
    var cancAPI = CancioneroAPI({ UserHelper.getUserID(this) })
    var selectedTags = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_song)
        val btnCreateSong = findViewById<Button>(R.id.btnCreateSong)
        val btnUpdateSong = findViewById<Button>(R.id.btnUpdateSong)
        val tagsLayout = findViewById<FlexboxLayout>(R.id.tagsLayout)
        val etxtSongTags = findViewById<AutoCompleteTextView>(R.id.etxtSongTags)

        if(intent.extras != null){
            title = getString(R.string.updateSong_title)
            btnUpdateSong.isEnabled = true
            btnCreateSong.isEnabled = false
            //Para Editar Cancion
            val bundle = intent.extras!!
            songID = bundle.getInt("songId")
            etxtSongTitle.setText(bundle.getString("songTitle"))
            etxtSongArtist.setText(bundle.getString("songArtist"))
            etxtSongLyrics.setText(bundle.getString("songLyrics"))
            formatTags(bundle.getString("songTags")!!)
//            etxtSongTags.setText(bundle.getString("songTags"))
        }
        else{
            title = getString(R.string.addSong_title)
            btnUpdateSong.isEnabled = false
            btnCreateSong.isEnabled = true
        }

        btnCreateSong.setOnClickListener {
            val id = 0
            val title = etxtSongTitle.text.toString()
            val artist = etxtSongArtist.text.toString()
            val lyrics = etxtSongLyrics.text.toString()

            val newSong = Song(
                id,
                title,
                artist,
                lyrics,
                selectedTags.joinToString(", ")
            )
            addSongDB(newSong)
        }

        btnUpdateSong.setOnClickListener {
            val title = etxtSongTitle.text.toString()
            val artist = etxtSongArtist.text.toString()
            val lyrics = etxtSongLyrics.text.toString()

            val currSong = Song(
                songID,
                title,
                artist,
                lyrics,
                selectedTags.joinToString(", ")
            )
            updateSongDB(currSong)
        }
        val tagsList = listOf("Entrada", "Piedad", "Gloria", "Salmo", "Proclamacion", "Ofertorio", "Paz", "Cordero", "Comunion", "Reflexion", "Maria", "Salida", "Ordinario", "Cuaresma", "Pascua", "Pentecostes", "Adviento", "Navidad")
        etxtSongTags.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
            tagsList))

        etxtSongTags.setOnClickListener {
            etxtSongTags.showDropDown()
        }
        etxtSongTags.setOnItemClickListener { parent, view, position, id ->
            addNewChip((view as AppCompatTextView).text.toString(), tagsLayout)
            etxtSongTags.setText("")
        }

    }

    private fun addSongDB(song : Song){
        //Adds the song in DB
        cancAPI.addSong(song,
            success = { newSongID ->
                songID = newSongID
                Toast.makeText(applicationContext, getString(R.string.toast_createSong), Toast.LENGTH_SHORT).show()
                finish()
            })
        //TODO: Create a fail behaviour?
    }

    private fun updateSongDB(song : Song){
        //Updates the song in DB
        cancAPI.updateSong(song,
            success = {
                Toast.makeText(applicationContext, getString(R.string.toast_updateSong), Toast.LENGTH_SHORT).show()
                finish()
            })
        //TODO: Create a fail behaviour?
    }

    private fun addNewChip(tag: String, chipGroup: FlexboxLayout){
//        val lp = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
//        lp.setMargins(-10,-20,-10,-20)
        val chip = layoutInflater.inflate(R.layout.chip_elem, chipGroup, false) as Chip
        chip.text = tag
        chip.isCloseIconVisible = true
        chip.isClickable = true
//        chip.layoutParams = lp

        if(!selectedTags.contains(chip.text.toString())) {
            selectedTags.add(chip.text.toString())
            chipGroup.addView(chip as View, chipGroup.childCount -1)
        }

        chip.setOnCloseIconClickListener {
            selectedTags.remove(chip.text.toString())
            chipGroup.removeView(chip as View)
        }
    }

    private fun formatTags(tagsConcatenated: String){
        if(tagsConcatenated == "") etxtSongTags.setText("")
        else{
            val tagsArr = tagsConcatenated.split(", ")
            for(tag in tagsArr){
                addNewChip(tag, tagsLayout)
            }
        }
    }

}