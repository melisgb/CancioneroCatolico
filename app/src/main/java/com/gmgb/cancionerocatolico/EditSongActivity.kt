package com.gmgb.cancionerocatolico

import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.gmgb.cancionerocatolico.R
import com.gmgb.cancionerocatolico.api.CancioneroAPI
import com.gmgb.cancionerocatolico.objects.Song
import com.gmgb.cancionerocatolico.utils.UserHelper
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_edit_song.*


/* Activity to CREATE or EDIT a SONG */
class EditSongActivity : AppCompatActivity() {
    private var songID : Int = 0
    private var cancAPI = CancioneroAPI({
        UserHelper.getUserID(this)
    })
    private var selectedTags = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_song)
        val btnCreateSong = findViewById<Button>(R.id.btnCreateSong)
        val btnUpdateSong = findViewById<Button>(R.id.btnUpdateSong)
        val btnSendSuggestion = findViewById<Button>(R.id.btnSendSuggestion)
        val tagsLayout = findViewById<FlexboxLayout>(R.id.tagsLayout)
        val etxtSongTags = findViewById<AutoCompleteTextView>(R.id.etxtSongTags)

        if(intent.extras != null){
            //A song to edit or suggest change
            title = getString(R.string.updateSong_title)
            btnUpdateSong.visibility = View.VISIBLE
            btnSendSuggestion.visibility = View.INVISIBLE
            btnCreateSong.visibility = View.INVISIBLE
            //Para Editar Cancion
            val bundle = intent.extras!!
            songID = bundle.getInt("songId")
            etxtSongTitle.setText(bundle.getString("songTitle"))
            etxtSongArtist.setText(bundle.getString("songArtist"))
            etxtSongLyrics.setText(bundle.getString("songLyrics"))
            formatTags(bundle.getString("songTags")!!)
            val onEditMode = bundle.getBoolean("inEditMode")
            if(!onEditMode) {
                //Suggest view
                title = getString(R.string.suggestSong_title)
                btnUpdateSong.setText(R.string.button_suggestChange)
                btnUpdateSong.visibility = View.INVISIBLE
                btnSendSuggestion.visibility = View.VISIBLE
                etxtSongTitle.isEnabled = false
                etxtSongArtist.isEnabled = false
                tagsLayout.visibility = View.GONE

                //Guide link to block the edition in the view -> http://jtdz-solenoids.com/stackoverflow_/questions/6275299/how-to-disable-copy-paste-from-to-edittext
                etxtSongLyrics.customSelectionActionModeCallback = object : ActionMode.Callback {
                    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                        return false
                    }
                    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        return false
                    }
                    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        return false
                    }
                    override fun onDestroyActionMode(mode: ActionMode?) {
                    }
                }

            }
        }
        else{
            title = getString(R.string.addSong_title)
            btnUpdateSong.visibility = View.INVISIBLE
            btnSendSuggestion.visibility = View.INVISIBLE
            btnCreateSong.visibility = View.VISIBLE
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

        btnSendSuggestion.setOnClickListener {
            val lyricsSuggestions = etxtSongLyrics.text.toString()
            sendSongSuggestion(songID, lyricsSuggestions)
        }
        //Possible elements: Entrada, Piedad, Gloria, Salmo, Proclamacion, Ofertorio, Santo, Paz, Cordero, Comunion, Reflexion, Maria, Padre Nuestro, Salida, Ordinario, Cuaresma, Semana Santa, Pascua, Pentecostes, Adviento, Navidad, Avivamiento, Convivencia, Invocacion
        val tagsList = listOf("Entrada", "Piedad", "Gloria", "Salmo", "Proclamacion", "Ofertorio", "Santo", "Paz", "Cordero", "Comunion", "Reflexion", "Maria", "Padre Nuestro", "Salida", "Ordinario", "Cuaresma", "Semana Santa", "Pascua", "Pentecostes", "Adviento", "Navidad", "Avivamiento", "Convivencia", "Invocacion")
        etxtSongTags.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
            tagsList.sorted()))

        etxtSongTags.setOnClickListener {
            etxtSongTags.showDropDown()
        }
        etxtSongTags.setOnFocusChangeListener { v, hasFocus ->
            val dividerSongTags = findViewById<ImageView>(R.id.dividerSongTags)!!
            if(hasFocus){
                dividerSongTags.setBackgroundColor(resources.getColor(R.color.accentColor))
            }
            else  {
                dividerSongTags.setBackgroundColor(resources.getColor(R.color.primaryColor))
            }
        }
        etxtSongTags.setOnItemClickListener { _, view, _, _ ->
            addNewChip((view as AppCompatTextView).text.toString(), tagsLayout)
            etxtSongTags.setText("")
        }

    }

    private fun addSongDB(song : Song){
        //Adds the song in DB
        cancAPI.addSong(song,
            success = { newSongID ->
                songID = newSongID
                Toast.makeText(applicationContext, getString(R.string.toast_song_created), Toast.LENGTH_SHORT).show()
                finish()
            })
        //TODO: Create a fail behaviour?
    }

    private fun updateSongDB(song : Song){
        //Updates the song in DB
        cancAPI.updateSong(song,
            success = {
                Toast.makeText(applicationContext, getString(R.string.toast_song_updated), Toast.LENGTH_SHORT).show()
                finish()
            })
        //TODO: Create a fail behaviour?
    }

    private fun sendSongSuggestion(songID : Int, lyricsSuggestions : String){
        //Updates the song in DB
        cancAPI.addSongSuggestion(songID, lyricsSuggestions,
            success = {
                Toast.makeText(applicationContext, getString(R.string.toast_suggestion_sent), Toast.LENGTH_SHORT).show()
                finish()
            })
    }

    private fun addNewChip(tag: String, chipGroup: FlexboxLayout){
        val chip = layoutInflater.inflate(R.layout.chip_elem, chipGroup, false) as Chip
        chip.text = tag
        chip.isCloseIconVisible = true
        chip.isClickable = true

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