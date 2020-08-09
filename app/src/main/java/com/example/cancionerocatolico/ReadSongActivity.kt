package com.example.cancionerocatolico

import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.Chord
import com.example.cancionerocatolico.objects.LyricsLine
import com.example.cancionerocatolico.objects.Note
import com.example.cancionerocatolico.objects.UserInfo
import com.example.cancionerocatolico.utils.Lyrics
import com.example.cancionerocatolico.utils.UserHelper
import kotlinx.android.synthetic.main.activity_read_song.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ReadSongActivity : AppCompatActivity() {
    var song_id : Int = 0
    var cancAPI = CancioneroAPI({ UserHelper.getUserID(this) })
    var lyricLines = listOf<LyricsLine>()
    var transposedLevel = 0
    var transformedLyricLine = listOf<LyricsLine>()
    val soundPool = SoundPool.Builder().setMaxStreams(4).build()
    val notesMap = HashMap<Note, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_song)
        loadAllNotes()
        title = getString(R.string.readSong_title)
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
        val editSongItem = menu!!.findItem(R.id.action_editSong)
        editSongItem.isVisible = UserHelper.getUserID(this)==1 || UserHelper.getUserID(this)==2
        val deleteSongItem = menu.findItem(R.id.action_deleteSong)
        deleteSongItem.isVisible = UserHelper.getUserID(this)==1 || UserHelper.getUserID(this)==2

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
                                                Toast.makeText(applicationContext,getString(R.string.toast_song_added_toList)+ selected_ListName,Toast.LENGTH_SHORT).show()
                                            })
                                    })
                            }
                            else {
                                //   When there is a LISTID / the list is already created
                                cancAPI.insertToList(listID!!, song_id.toString(),
                                    success = {
                                        Toast.makeText(applicationContext,getString(R.string.toast_song_added_toList)+ selected_ListName,Toast.LENGTH_SHORT).show()
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
                                            Toast.makeText(applicationContext,getString(R.string.toast_song_added_toList)+ selected_ListName,Toast.LENGTH_SHORT).show()
                                        })
                                })
                        }
                        else {
                           cancAPI.insertToList(listID!!, song_id.toString(),
                                success = {
                                    Toast.makeText(applicationContext,getString(R.string.toast_song_added_toList)+ selected_ListName,Toast.LENGTH_SHORT).show()
                                })
                        }
                    })
                true
            }
            R.id.action_editSong-> {
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
                        Toast.makeText( applicationContext, getString(R.string.toast_song_deleted), Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    fail = {
                        Toast.makeText( applicationContext,getString(R.string.toast_song_unable_to_delete), Toast.LENGTH_SHORT).show()
                    }
                )
                true
            }
            R.id.action_shareSong -> {
                val songTitle = txtvReadSongTitle.text.toString()
                val songArtist = txtvReadSongArtist.text.toString()
                val songLyrics = txtvReadSongLyrics.text.toString()
                val songTags = txtvReadSongTags.text.toString()

                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Cancion: $songTitle")

                val stringOfSongs = StringBuilder()
                stringOfSongs.append("*Cancion ")
                stringOfSongs.append("$songTitle - $songArtist*\n\n")
                stringOfSongs.append("$songLyrics \n\n")
                stringOfSongs.append("Tags: $songTags")
                sharingIntent.putExtra(Intent.EXTRA_TEXT, stringOfSongs.toString())
                applicationContext.startActivity(Intent.createChooser(sharingIntent, "Share via")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                Toast.makeText(this, getString(R.string.toast_shareSong), Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

    private fun parseLyrics(lyrics : String) : ArrayList<LyricsLine> {
        return Lyrics.parseLyricsFromSong(lyrics)
    }

    private fun lyricsToSpannable(lyricLine : LyricsLine) : Spannable {
        //Sets the style depending on the type of Line it is, and for the chords will make each chord as playable
        val lineToSpan: Spannable = SpannableString(lyricLine.line)
        if(lyricLine.type == LyricsLine.LyricsLineType.VERSE){
            lineToSpan.setSpan(
                ForegroundColorSpan(Color.BLACK), 0, lyricLine.line.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        else if(lyricLine.type == LyricsLine.LyricsLineType.CHORDS){
            val chordsPatt = """(D[oO]|R[eE]|M[iI]|F[aA]|S(ol|OL)|L[aA]|S[iI]|A|B|C|D|E|F|G)(#|b)?(m)?(sus|maj)?([2-9])?""".toRegex()

            val allOcurrences = ArrayList<MatchResult>()
            allOcurrences.addAll(chordsPatt.findAll(lyricLine.line))

            for(ocurr in allOcurrences){
                val chordInOcurr = ocurr.value.toLowerCase().capitalize()
                val chord = Chord.values().find { chord -> chord.chordAme == chordInOcurr || chord.chordLat == chordInOcurr }
                if(chord==null) continue

                val clickableSpan = object : ClickableSpan(){
                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = Color.BLUE
                    }
                    override fun onClick(widget: View) {
                        for(note in chord.notes){
                            val noteID = notesMap.get(note)

                            if(noteID!=null) soundPool.play(noteID, 1.0f/chord.notes.size, 1.0f/chord.notes.size, 1, 0, 1f)
                        }
                    }
                }
                lineToSpan.setSpan(
                    clickableSpan, ocurr.range.first, ocurr.range.last+1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
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
        txtvReadSongLyrics.movementMethod = LinkMovementMethod.getInstance()
    }

    fun loadAllNotes(){
        for(note in Note.values()){
            notesMap.put(note, soundPool.load(applicationContext, note.noteResourceId, 1))
        }
    }
}