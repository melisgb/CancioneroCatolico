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
    var cancAPI = CancioneroAPI()
    var listsRecyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_my_lists)

        title = getString(R.string.view_my_lists_title)
        listsRecyclerView = findViewById(R.id.recyclervMyLists)
//        generateList(20) //dummy data
        getSummaryLists(success = {
            listsAdapter = MyListAdapter(applicationContext, listOfListsSongs)
            listsRecyclerView!!.adapter = listsAdapter
            listsRecyclerView!!.layoutManager = LinearLayoutManager(this)
        })

        //notify the insertion in RecyclerView
//        listOfListsSongs.add(0,
//            ListSongs(100, "Favorites 100", hashMapOf()))
//        listsAdapter!!.notifyItemInserted(0) //required in RecyclerView
    }

    override fun onRestart() {
        getSummaryLists(success = {
            listsAdapter = MyListAdapter(applicationContext, listOfListsSongs)
            listsRecyclerView!!.adapter = listsAdapter
            listsRecyclerView!!.layoutManager = LinearLayoutManager(this)
        })
        super.onRestart()

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

    fun getSummaryLists(success : (Any?) -> Unit){
        cancAPI.loadSummaryLists( success = { listOfLists ->
            listOfListsSongs.clear()
            listOfListsSongs.addAll(listOfLists)
            success(null)
        })
    }

    fun deleteList(listID : Int, success : (Boolean) -> Unit){
        cancAPI.removeWholeList(listID, success = { result ->
            success(true)
        })
    }

}