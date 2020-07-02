package com.example.cancionerocatolico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class ViewSongsActivity : AppCompatActivity() {
    var songsList = ArrayList<Song>()
    var songsAdapter : SongAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_songs)


        songsAdapter = SongAdapter(this, songsList)
        var songsListView = findViewById<ListView>(R.id.lvListofSongs)
        songsListView.adapter = songsAdapter

        songsList.clear()
        loadSongs()

    }

    fun loadSongs(){
        /*dummy data*/
        songsList.add(
            Song(1, "Un padre como el nuestro", "Entrenados", "Ula ula ula ula", "Entrada, Salmos"))
        songsList.add(
            Song(2, "Solamente una vez", "Entrenados", "Ula ula ula ula", "Paz, Salmos"))
        songsList.add(
            Song(3, "Una vez nada mas", "Entrenados", "Ula ula ula ula", "Paz, Salmos"))

    }
}