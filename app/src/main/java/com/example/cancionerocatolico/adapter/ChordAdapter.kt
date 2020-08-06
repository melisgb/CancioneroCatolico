package com.example.cancionerocatolico.adapter

import android.app.Activity
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.cancionerocatolico.R
import com.example.cancionerocatolico.objects.Chord
import kotlinx.android.synthetic.main.element_play_chords.view.*

class ChordAdapter(val context: Activity, val chordsList: List<Chord>) : BaseAdapter(){
    val layoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var currentChord = chordsList[position]

        val myElementView = layoutInflater.inflate(R.layout.element_play_chords, null)
        myElementView.btnChord.text = currentChord.chordAme

        myElementView.btnChord.setOnClickListener {
            val chord = chordsList[position] //calling getItemId
//            Chord.valueOf(chord.toString())
            val currMediaPlayer = MediaPlayer.create(context, currentChord.chordUrl)
            currMediaPlayer!!.setOnCompletionListener(
                { currMediaPlayer!!.release() }
            )
            currMediaPlayer!!.start()
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
}