package com.example.cancionerocatolico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        //notify the insertion
        listOfListsSongs.add(0,
            ListSongs(100, "Favorites 100", arrayListOf(1, 2, 3)))
        listsAdapter!!.notifyItemInserted(0) //required in RecyclerView

        listsRecyclerView.adapter = listsAdapter
        listsRecyclerView.layoutManager = LinearLayoutManager(this)



    }

    fun generateList(qty : Int){
        for(x in 1 until qty){
            val a = Random.nextInt()
            val b = Random.nextInt()
            val c = Random.nextInt()

            listOfListsSongs.add(
                ListSongs(x, "Favorites ${x}", arrayListOf(a, b, c)))
        }
    }
}