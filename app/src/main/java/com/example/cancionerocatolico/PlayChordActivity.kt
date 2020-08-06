package com.example.cancionerocatolico

import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.example.cancionerocatolico.adapter.ChordAdapter
import com.example.cancionerocatolico.objects.Chord

class PlayChordActivity : AppCompatActivity() {
    var chordsAdapter : ChordAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_chords)

        val chordsList = Chord.values().sortedBy { it.name }.toList()

        val gridView = findViewById<GridView>(R.id.gridvChords)
        chordsAdapter = ChordAdapter(this, chordsList)
        gridView.adapter = chordsAdapter
//
//        gridView.setOnItemClickListener { parent, view, position, id ->
//            val chord = chordsList[position] //calling getItemId
////            Chord.valueOf(chord.toString())
//            val currMediaPlayer = MediaPlayer.create(this, chord.chordUrl)
//                currMediaPlayer!!.setOnCompletionListener(
//                    { currMediaPlayer!!.release() }
//                )
//                currMediaPlayer!!.start()
//        }
//

    }
}