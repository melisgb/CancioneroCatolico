package com.example.cancionerocatolico

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.LyricsLine
import com.example.cancionerocatolico.utils.Lyrics
import com.example.cancionerocatolico.utils.UserHelper
import kotlinx.android.synthetic.main.activity_read_song.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ReadSongActivity : AppCompatActivity() {
    var song_id : Int = 0
    var cancAPI = CancioneroAPI({ UserHelper.getUserID(this) })
    var lyricLines = listOf<LyricsLine>()
    var transposedLevel = 0
    var transformedLyricLine = listOf<LyricsLine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_song)

        song_id = intent.extras!!.getInt("song_id")
        loadSong(song_id)
    }

    override fun onRestart() {
        loadSong(song_id)
        super.onRestart()
    }

    private fun loadSong(songID : Int)  {
        cancAPI.readSong(songID, 
            success = { song ->
                txtvReadSongTitle.text = song.songTitle
                txtvReadSongArtist.text = song.songArtist
                txtvReadSongTags.text = song.songTags

                lyricLines = parseLyrics(song.songLyrics)
                transformedLyricLine = lyricLines
                showLyricsInTextView()
            }
        )
    }

    override fun onCreateOptionsMenu( menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.read_song_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        return when (item.itemId) {
            R.id.action_increase_note -> {
                if(transposedLevel == 11){
                    transposedLevel = 0
                }
                else{
                    ++transposedLevel
                }
                transformedLyricLine = increaseSemiNote(transposedLevel)
                showLyricsInTextView()
                true
            }
            R.id.action_decrease_note -> {
                if(transposedLevel == 0) {
                    transposedLevel = 11
                }
                else{
                    --transposedLevel
                }
                transformedLyricLine = increaseSemiNote(transposedLevel)
                showLyricsInTextView()
                true
            }
            R.id.action_addSongToList -> {
                cancAPI.loadSummaryLists(
                    //get all listSongs summarized from DB
                    success = { listOfLists  ->
                        val listNamesA = ArrayList<String>()
                        for(list in listOfLists) {
                            listNamesA.add(list.listSongsName)
                        }
                        //to create New list
                        val df = SimpleDateFormat("dd_MM_yy_HHmmss")
                        val currDate = Date()
                        listNamesA.add("Lista"+ df.format(currDate))
                        val listNames = listNamesA.toArray(emptyArray<String>())
                        var selected_ListName = ""

                        val builder = AlertDialog.Builder(this@ReadSongActivity)
                        builder.setTitle(R.string.choose_list)
                        builder.setIcon(R.drawable.ic_add_to_list)
                        builder.setSingleChoiceItems(listNames, -1)  { dialogInterface, i ->
                            selected_ListName = listNames[i]

                            var listID = listOfLists.find{ l -> l.listSongsName == selected_ListName }?.listSongsID
//
                            if(listID == null){
                                //insert new list
                                cancAPI.createList(selected_ListName,
                                    success = { newlistID ->
                                        listID = newlistID
                                        cancAPI.insertToList(listID!!, song_id.toString(),
                                            success = {
                                                Toast.makeText(applicationContext,"Cancion agregada a $selected_ListName",Toast.LENGTH_SHORT).show()
                                            })
                                    })
                            }
                            else {
                                //   When there is a LISTID / the list is already created
                                cancAPI.insertToList(listID!!, song_id.toString(),
                                    success = {
                                        Toast.makeText(applicationContext,"Cancion agregada a $selected_ListName",Toast.LENGTH_SHORT).show()
                                    })
                            }
                            dialogInterface.dismiss()
                        }
                        builder.setNeutralButton("Cancelar") { dialog, _ ->
                            dialog.cancel()
                        }
                        val mDialog = builder.create()
                        mDialog.show()
                    }
                )
                true
            }
            R.id.action_addSongToFavs -> {
                cancAPI.loadSummaryLists(
                    //get all listSongs summarized from DB
                    success = { listOfLists ->
                        val selected_ListName = "Favoritos"
                        var listID = listOfLists.find{ l -> l.listSongsName == selected_ListName }?.listSongsID

                        if(listID == null){
                            cancAPI.createList("Favoritos",
                                success = {newlistID ->
                                    listID = newlistID
                                    cancAPI.insertToList(listID!!, song_id.toString(),
                                        success = {
                                            Toast.makeText(applicationContext,"Cancion agregada a $selected_ListName",Toast.LENGTH_SHORT).show()
                                        })

                                })
                        }
                        else {
                           cancAPI.insertToList(listID!!, song_id.toString(),
                                success = {
                                    Toast.makeText(applicationContext,"Cancion agregada a $selected_ListName",Toast.LENGTH_SHORT).show()
                                })
                        }
                    })
                true
            }
            R.id.action_editSong -> {
                val intent = Intent(applicationContext, EditSongActivity::class.java )
                intent.putExtra("songId", song_id)
                intent.putExtra("songTitle", txtvReadSongTitle.text.toString())
                intent.putExtra("songArtist", txtvReadSongArtist.text.toString())
                intent.putExtra("songLyrics", txtvReadSongLyrics.text.toString())
                intent.putExtra("songTags", txtvReadSongTags.text.toString())
                startActivity(intent)
                true
            }
            R.id.action_deleteSong-> {
                //Important notice: When an ADMIN user deletes a song it will get deleted from every list.
                cancAPI.deleteSong(
                    song_id,
                    success = {
                        Toast.makeText( applicationContext,"Cancion eliminada", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    fail = {
                        Toast.makeText( applicationContext,"No se pudo eliminar, pertenece a una lista", Toast.LENGTH_SHORT).show()
                    }
                )
                true
            }
            else -> false
        }
    }

    private fun parseLyrics(lyrics : String) : ArrayList<LyricsLine> {
        return Lyrics.parseLyricsFromSong(lyrics)
    }

    private fun lyricsToSpannable(lyricLine : LyricsLine) : Spannable {
        val lineToSpan: Spannable = SpannableString(lyricLine.line)
        if(lyricLine.type == LyricsLine.LyricsLineType.VERSE){
            lineToSpan.setSpan(
                ForegroundColorSpan(Color.BLACK), 0, lyricLine.line.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        else if(lyricLine.type == LyricsLine.LyricsLineType.CHORDS){
            lineToSpan.setSpan(
                ForegroundColorSpan(Color.BLUE), 0, lyricLine.line.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return lineToSpan
    }

    private fun <T> Iterable<T>.joinToSpannedString(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", limit: Int = -1, truncated: CharSequence = "...", transform: ((T) -> CharSequence)? = null): SpannedString {
        return joinTo(SpannableStringBuilder(), separator, prefix, postfix, limit, truncated, transform)
            .let { SpannedString(it) }
    }

    private fun increaseSemiNote(level : Int) : ArrayList<LyricsLine> {
        val lyricsArray = ArrayList<LyricsLine>()

        for (lyr in lyricLines) {
            if (lyr.type == LyricsLine.LyricsLineType.CHORDS) {
                val newChords = Lyrics.increaseSemiNote(lyr.line, level)
                val newLyr = LyricsLine(newChords, lyr.type)
                lyricsArray.add(newLyr)
            } else {
                lyricsArray.add(lyr)
            }
        }

        return lyricsArray
    }

    private fun showLyricsInTextView(){
        val spannArray = ArrayList<Spannable>()
        for(lyr in transformedLyricLine){
            spannArray.add(lyricsToSpannable(lyr))
        }
        txtvReadSongLyrics.text = spannArray.joinToSpannedString("\n")
    }
}