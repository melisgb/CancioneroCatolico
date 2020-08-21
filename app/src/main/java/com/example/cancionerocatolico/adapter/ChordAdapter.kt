package com.example.cancionerocatolico.adapter

import android.app.Activity
import android.app.Dialog
import android.media.SoundPool
import android.view.*
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.cancionerocatolico.R
import com.example.cancionerocatolico.objects.Chord
import com.example.cancionerocatolico.objects.Note
import kotlinx.android.synthetic.main.element_play_chords.view.*

class ChordAdapter(val context: Activity, var chordsList: List<Chord>) : BaseAdapter(){
    val layoutInflater = LayoutInflater.from(context)
    val soundPool = SoundPool.Builder().setMaxStreams(4).build()
    val notesMap = HashMap<Note, Int>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var currentChord = chordsList[position]

        val myElementView = layoutInflater.inflate(R.layout.element_play_chords, null)
        myElementView.btnChord.text = currentChord.chordAme

        myElementView.btnChord.setOnClickListener {
            val chord = chordsList[position] //calling getItemId
            for(note in chord.notes){
                val noteID = notesMap.get(note)
                if(noteID!=null) soundPool.play(noteID, 1.0f/chord.notes.size, 1.0f/chord.notes.size, 1, 0, 1f)
            }
        }

        myElementView.btnChord.setOnLongClickListener {
            val chord = chordsList[position] //calling getItemId
            showChordPopupMenu(chord.imgPath)
            true
        }
        return myElementView
    }

    override fun getItem(position: Int): Any {
        return chordsList[position]
    }

    override fun getItemId(position: Int): Long {
        return chordsList[position].ordinal.toLong()
    }

    override fun getCount(): Int {
        return chordsList.size
    }

    fun loadAllNotes(){
        for(note in Note.values()){
            notesMap.put(note, soundPool.load(context, note.noteResourceId, 1))
        }
    }

    private fun showChordPopupMenu(imagePath: Int){
        val chordView = layoutInflater.inflate(R.layout.dialog_view_chord, null)
        val chordImage = chordView.findViewById<ImageView>(R.id.ivChord)
        chordImage.setImageResource(imagePath)
        val viewChordDialog = Dialog(context)
        viewChordDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        viewChordDialog.setContentView(chordView)
        viewChordDialog.show()

    }
}