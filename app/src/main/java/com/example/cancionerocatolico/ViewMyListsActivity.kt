package com.example.cancionerocatolico

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class ViewMyListsActivity : AppCompatActivity() {
    var listsAdapter : MyListAdapter? = null
    var listOfListsSongs = ArrayList<ListSongs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_my_lists)

        val listsRecyclerView : RecyclerView = findViewById(R.id.recyclervMyLists)

        listOfListsSongs.clear()

        //dummy data
        generateList(20)

        listsAdapter = MyListAdapter(listOfListsSongs)

        //notify the insertion in RecyclerView
//        listOfListsSongs.add(0,
//            ListSongs(100, "Favorites 100", hashMapOf()))
//        listsAdapter!!.notifyItemInserted(0) //required in RecyclerView

        listsRecyclerView.adapter = listsAdapter
        listsRecyclerView.layoutManager = LinearLayoutManager(this)

        val demoBtn = findViewById<Button>(R.id.demoButton)
        demoBtn.setOnClickListener {
            val intent = Intent(this, ViewSpecificListActivity::class.java)
            startActivity(intent)
        }



    }

    fun generateList(qty : Int){
        for(x in 1 until qty){
            val randSong = Song(100, "Un padre como el nuestro", "Entrenados", "Ula ula ula ula", "Entrada, Salmos")
            var hashMap = HashMap<Int, Song>()
            hashMap.put(1, randSong)
            hashMap.put(2, randSong)
            hashMap.put(3, randSong)

            listOfListsSongs.add(
                ListSongs(x, "Favorites ${x}", hashMap))
        }
    }
}