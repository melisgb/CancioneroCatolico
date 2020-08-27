package com.example.cancionerocatolico

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.SoundPool
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.LinearInterpolator
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.Chord
import com.example.cancionerocatolico.objects.LyricsLine
import com.example.cancionerocatolico.objects.Note
import com.example.cancionerocatolico.utils.Lyrics
import com.example.cancionerocatolico.utils.UserHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_read_song.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ReadSongActivity : AppCompatActivity() {
    private var songID : Int = 0
    private var cancAPI = CancioneroAPI({ UserHelper.getUserID(this) })
    private var lyricLines = listOf<LyricsLine>()
    private var transposedLevel = 0
    private var isSameLanguage = true
    private var transformedLyricLine = listOf<LyricsLine>()
    val soundPool: SoundPool = SoundPool.Builder().setMaxStreams(4).build()
    val notesMap = HashMap<Note, Int>()
    var isFABOpen = false
    lateinit var fabShowFabMenu : FloatingActionButton
    lateinit var fabIncreaseNote : FloatingActionButton
    lateinit var fabDecreaseNote : FloatingActionButton
    lateinit var fabTranslate : FloatingActionButton
    lateinit var fabAutoScroll : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_song)
        loadAllNotes()
        title = getString(R.string.readSong_title)
        songID = intent.extras!!.getInt("song_id")
        loadSong(songID)

        fabShowFabMenu = findViewById<FloatingActionButton>(R.id.fabShowSongOptions)
        fabIncreaseNote = findViewById<FloatingActionButton>(R.id.fabIncreaseNote)
        fabDecreaseNote = findViewById<FloatingActionButton>(R.id.fabDecreaseNote)
        fabTranslate = findViewById<FloatingActionButton>(R.id.fabTranslateNotes)
        fabAutoScroll = findViewById<FloatingActionButton>(R.id.fabAutoScrollLyrics)
        fabShowFabMenu.setOnClickListener(View.OnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        })

        fabIncreaseNote.setOnClickListener {
            if(transposedLevel == 11){
                transposedLevel = 0
            }
            else{
                ++transposedLevel
            }
            showLyricsInTextView()
        }
        fabDecreaseNote.setOnClickListener {
            if(transposedLevel == 0) {
                transposedLevel = 11
            }
            else{
                --transposedLevel
            }
            showLyricsInTextView()
        }
        fabTranslate.setOnClickListener {
            isSameLanguage = !isSameLanguage //change the boolean to the opposite
            showLyricsInTextView()
        }
        fabAutoScroll.setOnClickListener {
            scrollSmoothly(1) //move 1 pixel down
        }
    }
    private fun scrollSmoothly(y : Int){
        //Base link -> https://stackoverflow.com/questions/27541386/programmatically-control-the-scrolling-speed-of-scrollview-android
        verticalLyrScroll.smoothScrollBy(0, y)
        val yPosition = verticalLyrScroll.height + verticalLyrScroll.scrollY
        val fullHeight = verticalLyrScroll.getChildAt(0).height + 50 //fixed 50 due to padding
        if(yPosition < fullHeight)  verticalLyrScroll.postDelayed({scrollSmoothly(y)}, 110)
    }

    //Help on:
    // https://stackoverflow.com/questions/30699302/android-design-support-library-expandable-floating-action-buttonfab-menu#:~:text=First%20create%20the%20menu%20layouts,per%20your%20need%20and%20number.
    private fun showFABMenu() {
        isFABOpen = true
        fabShowFabMenu.setImageResource(R.drawable.ic_close_fab)
        fabShowFabMenu.animate().rotationBy(180f)
        fabIncreaseNote.animate().translationY(-resources.getDimension(R.dimen.standard_65))
        fabDecreaseNote.animate().translationY(-resources.getDimension(R.dimen.standard_105))
        fabTranslate.animate().translationY(-resources.getDimension(R.dimen.standard_155))
        fabAutoScroll.animate().translationY(-resources.getDimension(R.dimen.standard_205))
    }

    private fun closeFABMenu() {
        isFABOpen = false
        fabShowFabMenu.setImageResource(R.drawable.ic_open_fab)
        fabShowFabMenu.animate().rotation(0f)
        fabIncreaseNote.animate().translationY(0f)
        fabDecreaseNote.animate().translationY(0f)
        fabTranslate.animate().translationY(0f)
        fabAutoScroll.animate().translationY(0f)
    }
    override fun onBackPressed() {
        if (isFABOpen) {
            closeFABMenu()
        } else {
            super.onBackPressed()
        }
    }

    override fun onRestart() {
        loadSong(songID)
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
                        var selectedListName = ""

                        val builder = AlertDialog.Builder(this@ReadSongActivity)
                        builder.setTitle(R.string.choose_list)
                        builder.setIcon(R.drawable.ic_add_to_list_black)
                        builder.setSingleChoiceItems(listNames, -1)  { dialogInterface, i ->
                            selectedListName = listNames[i]

                            var listID = listOfLists.find{ l -> l.listSongsName == selectedListName }?.listSongsID

                            if(listID == null){
                                //insert new list
                                cancAPI.createList(selectedListName,
                                    success = { newlistID ->
                                        listID = newlistID
                                        cancAPI.insertToList(listID!!, songID.toString(),
                                            success = {
                                                Toast.makeText(applicationContext,getString(R.string.toast_song_added_toList, selectedListName),Toast.LENGTH_SHORT).show()
                                            })
                                    })
                            }
                            else {
                                //   When there is a LISTID / the list is already created
                                cancAPI.insertToList(listID!!, songID.toString(),
                                    success = {
                                        Toast.makeText(applicationContext,getString(R.string.toast_song_added_toList, selectedListName),Toast.LENGTH_SHORT).show()
                                    })
                            }
                            dialogInterface.dismiss()
                        }
                        builder.setNeutralButton(getString(R.string.cancel_dialog)) { dialog, _ ->
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
                        val selectedListName = "Favoritos"
                        var listID = listOfLists.find{ l -> l.listSongsName == selectedListName }?.listSongsID

                        if(listID == null){
                            cancAPI.createList(selectedListName,
                                success = {newlistID ->
                                    listID = newlistID
                                    cancAPI.insertToList(listID!!, songID.toString(),
                                        success = {
                                            Toast.makeText(applicationContext,getString(R.string.toast_song_added_toList, selectedListName),Toast.LENGTH_SHORT).show()
                                        })
                                })
                        }
                        else {
                           cancAPI.insertToList(listID!!, songID.toString(),
                                success = {
                                    Toast.makeText(applicationContext,getString(R.string.toast_song_added_toList, selectedListName),Toast.LENGTH_SHORT).show()
                                })
                        }
                    })
                true
            }
            R.id.action_editSong-> {
                val intent = Intent(applicationContext, EditSongActivity::class.java )
                intent.putExtra("songId", songID)
                intent.putExtra("songTitle", txtvReadSongTitle.text.toString())
                intent.putExtra("songArtist", txtvReadSongArtist.text.toString())
                intent.putExtra("songLyrics", txtvReadSongLyrics.text.toString())
                intent.putExtra("songTags", txtvReadSongTags.text.toString())
                intent.putExtra("inEditMode", true)
                startActivity(intent)
                true
            }
            R.id.action_deleteSong-> {
                //Important notice: When an ADMIN user deletes a song it will get deleted from every list.
                cancAPI.deleteSong(
                    songID,
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
            R.id.action_suggestEdit -> {
                val intent = Intent(this, EditSongActivity::class.java)
                intent.putExtra("songId", songID)
                intent.putExtra("songTitle", txtvReadSongTitle.text.toString())
                intent.putExtra("songArtist", txtvReadSongArtist.text.toString())
                intent.putExtra("songLyrics", txtvReadSongLyrics.text.toString())
                intent.putExtra("songTags", txtvReadSongTags.text.toString())
                intent.putExtra("inEditMode", false)
                startActivity(intent)
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
                ForegroundColorSpan(getColor(R.color.primaryTextColor)), 0, lyricLine.line.length,
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
                        ds.color = getColor(R.color.accentColor)
                    }
                    override fun onClick(widget: View) {
                        for(note in chord.notes){
                            val noteID = notesMap[note]

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
    //extension function
    private fun <T> Iterable<T>.joinToSpannedString(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", limit: Int = -1, truncated: CharSequence = "...", transform: ((T) -> CharSequence)? = null): SpannedString {
        return joinTo(SpannableStringBuilder(), separator, prefix, postfix, limit, truncated, transform)
            .let { SpannedString(it) }
    }

    private fun increaseSemiNote(lyricLines : List<LyricsLine>, level : Int) : ArrayList<LyricsLine> {
        val lyricsArray = ArrayList<LyricsLine>()

        for (lyr in lyricLines) {
            if (lyr.type == LyricsLine.LyricsLineType.CHORDS) {
                val newChords = Lyrics.increaseSemiNoteMultipleTimes(lyr.line, level)
                val newLyr = LyricsLine(newChords, lyr.type)
                lyricsArray.add(newLyr)
            } else {
                lyricsArray.add(lyr)
            }
        }

        return lyricsArray
    }

    private fun changeChordsLanguage(lyricLines : List<LyricsLine>, isSameLanguage: Boolean) : ArrayList<LyricsLine> {
        if(isSameLanguage) return ArrayList(lyricLines) //return without changes
        val lyricsArray = ArrayList<LyricsLine>()

        for (lyr in lyricLines) {
            if (lyr.type == LyricsLine.LyricsLineType.CHORDS) {
                val newChords = Lyrics.changeLanguage(lyr.line)
                val newLyr = LyricsLine(newChords, lyr.type)
                lyricsArray.add(newLyr)
            } else {
                lyricsArray.add(lyr)
            }
        }
        return lyricsArray
    }

    /*This function will handle two transformations of lyrics  :
      - Increasing seminote
      - Change Language
     */
    private fun showLyricsInTextView(){
        transformedLyricLine = increaseSemiNote(lyricLines, transposedLevel)
        transformedLyricLine = changeChordsLanguage(transformedLyricLine, isSameLanguage)

        val spannArray = ArrayList<Spannable>()
        for(lyr in transformedLyricLine){
            spannArray.add(lyricsToSpannable(lyr))
        }
        txtvReadSongLyrics.text = spannArray.joinToSpannedString("\n")
        txtvReadSongLyrics.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun loadAllNotes(){
        for(note in Note.values()){
            notesMap[note] = soundPool.load(applicationContext, note.noteResourceId, 1)
        }
    }
}