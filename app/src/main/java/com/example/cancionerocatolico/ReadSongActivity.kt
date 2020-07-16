package com.example.cancionerocatolico

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_read_song.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ReadSongActivity : AppCompatActivity() {
    var song_id : Int = 0
    var cancAPI = CancioneroAPI()
    val lyricsApi = Lyrics()
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

    fun loadSong(songID : Int)  {
        cancAPI.readSong(songID, 
            success = { song ->
                txtvReadSongTitle.text = song.songTitle
                txtvReadSongArtist.text = song.songArtist
                txtvReadSongTags.text = song.songTags

                val lyricsArray = transform(song.songLyrics)
                val spannArray = ArrayList<Spannable>()
                for(lyr in lyricsArray){
                    spannArray.add(lyricsToSpannable(lyr))
                }

                txtvReadSongLyrics.text = spannArray.joinToSpannedString("\n")
//                txtvReadSongLyrics.text = song.songLyrics
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
            R.id.action_addSongToList -> {
                cancAPI.loadSummaryLists(
                    //get all listSongs summarized from DB
                    success = { listOfLists  ->
                        var listNamesA = ArrayList<String>()
                        for(list in listOfLists) {
                            listNamesA.add(list.listSongsName)
                        }
                        //to create New list
                        val df = SimpleDateFormat("yy_MM_dd_HH_mm_ss")
                        val currDate = Date()
                        listNamesA.add("NuevaLista"+ df.format(currDate))

                        val listNames = listNamesA.toArray(emptyArray<String>())
                        var selected_ListName = ""
                        val builder = AlertDialog.Builder(this@ReadSongActivity)
                        builder.setTitle(R.string.choose_list)
                        builder.setIcon(R.drawable.ic_add_to_list)
                        builder.setSingleChoiceItems(listNames, -1)  { dialogInterface, i ->
                            selected_ListName = listNames[i]

                            var myListSongs = HashMap<Int, Song>()
                            myListSongs[song_id] = ViewSongsActivity.songsList[song_id]

                            var listID = listOfLists.find{ l -> l.listSongsName == selected_ListName }?.listSongsID
//
                            if(listID == null){
                                //insert new list
                                cancAPI.createList(selected_ListName,
                                    success = { newlistID ->
                                        listID = newlistID
                                        cancAPI.loadCurrentList(listID!!,
                                            success = { currentList ->
                                                cancAPI.insertToList(listID!!, song_id.toString())
                                                Toast.makeText(applicationContext,"Cancion agregada a ${selected_ListName}",Toast.LENGTH_SHORT).show()
                                            })
                                    })
                            }
                            else {
                                //   When there is a LISTID / the list is already created
                                cancAPI.loadCurrentList(listID!!,
                                    success = { currentList ->
                                        cancAPI.insertToList(listID!!, song_id.toString())
                                        Toast.makeText(applicationContext,"Cancion agregada a ${selected_ListName}",Toast.LENGTH_SHORT
                                        ).show()
                                    })
                            }
                            dialogInterface.dismiss()
                        }
                        builder.setNeutralButton("Cancelar") { dialog, which ->
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
                        var selected_ListName = "Favoritos"
                        val listID = listOfLists.find{ l -> l.listSongsName == selected_ListName }?.listSongsID
                        //   When there is a LISTID / the list is already created
                        cancAPI.loadCurrentList(listID!!,
                            success = { currentList ->
                                cancAPI.insertToList(listID!!, song_id.toString())
                                Toast.makeText(applicationContext,"Cancion agregada a ${selected_ListName}",Toast.LENGTH_SHORT).show()
                            })
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

    fun transform(lyrics : String) : ArrayList<LyricsLine> {
        return lyricsApi.transformLyrics(lyrics)
    }

    fun lyricsToSpannable(lyricLine : LyricsLine) : Spannable {
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

    fun <T> Iterable<T>.joinToSpannedString(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", limit: Int = -1, truncated: CharSequence = "...", transform: ((T) -> CharSequence)? = null): SpannedString {
        return joinTo(SpannableStringBuilder(), separator, prefix, postfix, limit, truncated, transform)
            .let { SpannedString(it) }
    }
}