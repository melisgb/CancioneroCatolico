package com.example.cancionerocatolico

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class ViewSpecificListActivity : AppCompatActivity() {
    var mySongsList = ArrayList<Song>()
    var songsAdapter : SongAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_specific_list)

        val extras = intent.extras
        val listID = extras!!.getInt("listID")


//        loadSongs() //dummydata
        getSongsCurrentList(
            listID,
            success = {
                mySongsList.clear()
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
    fun getSongsCurrentList(listID : Int, success : (ArrayList<Song>) -> Unit) {
        //Search into DB the specific songsList
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/get_songs.php?")
            .buildUpon()
            .appendQueryParameter("case", "2")
            .appendQueryParameter("listsong_id", listID.toString())
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
//                Toast.makeText(applicationContext, "Retrieving songs failed", Toast.LENGTH_SHORT).show()
            },
            onSuccess = { listOfSongs ->
//                Toast.makeText(applicationContext, "Loading songs", Toast.LENGTH_SHORT).show()
                success(listOfSongs as ArrayList<Song>)
            }
        ).execute(url)

    }

}