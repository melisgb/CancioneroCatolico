package com.example.cancionerocatolico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class ViewSpecificListActivity : AppCompatActivity() {
    var mySongsList = ArrayList<Song>()
    var songsAdapter : SongAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_specific_list)


        songsAdapter = SongAdapter(this, mySongsList)
        var songsListView = findViewById<ListView>(R.id.lvListofSongs)
        songsListView.adapter = songsAdapter

        mySongsList.clear()
        loadSongs()

    }

    fun loadSongs(){
        /*dummy data*/
        mySongsList.add(
            Song(8, "Carlos' Love", "Entrenados", "Ula ula ula ula", "Entrada, Salmos"))
        mySongsList.add(
            Song(9, "Melingo's Love", "Entrenados", "Ula ula ula ula", "Paz, Salmos"))
        mySongsList.add(
            Song(10, "Loving him", "Entrenados", "Ula ula ula ula", "Paz, Salmos"))

    }
}