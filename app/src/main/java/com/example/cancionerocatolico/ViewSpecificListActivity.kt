package com.example.cancionerocatolico

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast

class ViewSpecificListActivity : AppCompatActivity() {
    var mySongsList = ArrayList<Song>()
    var songsAdapter : SongAdapter? = null
    var cancAPI = CancioneroAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_specific_list)

        val extras = intent.extras
        val listID = extras!!.getInt("listID")

//        loadSongs() //to load dummydata
        getSongsCurrentList(
            listID,
            success = {
                songsAdapter = SongAdapter(this, mySongsList)
                var songsListView = findViewById<ListView>(R.id.lvSpecificList)
                songsListView.adapter = songsAdapter
            }
        )

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
    fun getSongsCurrentList(listID : Int, success : (Any?) -> Unit) {
        cancAPI.loadCurrentList(
            listID,
            success = { currentList ->
                mySongsList.clear()
                mySongsList.addAll(currentList)
                success(null)
        })
    }
}