package com.example.cancionerocatolico

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.cancionerocatolico.objects.Chord

class PlayChordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_chord)

        //creating buttons dynamically
        for (chord in Chord.values().sortedBy { it.name }) {
            val ll = findViewById<View>(R.id.linear) as LinearLayout

            val btn = Button(this)
            btn.text = chord.name
            btn.isAllCaps = false
            btn.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            btn.setOnClickListener {
                val currMediaPlayer = MediaPlayer.create(this, chord.chordUrl)
                currMediaPlayer!!.setOnCompletionListener(
                    { currMediaPlayer!!.release() }
                )
                currMediaPlayer!!.start()
            }
            ll.addView(btn)
        }

    }
}