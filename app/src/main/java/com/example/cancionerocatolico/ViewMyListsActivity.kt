package com.example.cancionerocatolico

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cancionerocatolico.adapter.MyListAdapter
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.ListSongs
import com.example.cancionerocatolico.utils.UserHelper

class ViewMyListsActivity : AppCompatActivity() {
    var listsAdapter : MyListAdapter? = null
    var listOfListsSongs = ArrayList<ListSongs>()
    var cancAPI = CancioneroAPI({ UserHelper.getUserID(this) })
    var listsRecyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_my_lists)

        title = getString(R.string.view_my_lists_title)
        listsRecyclerView = findViewById(R.id.recyclervMyLists)
//        cancAPI.generateList(20) //dummy data
        getSummaryLists(success = {
            listsAdapter = MyListAdapter(
                applicationContext,
                listOfListsSongs
            )
            listsRecyclerView!!.adapter = listsAdapter

            val itemDecorator = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
            itemDecorator.setDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.list_separator)!!)
            listsRecyclerView!!.addItemDecoration(itemDecorator)
            listsRecyclerView!!.layoutManager = LinearLayoutManager(this)
        })
    }

    override fun onRestart() {
        getSummaryLists(success = {
            listsAdapter = MyListAdapter(
                applicationContext,
                listOfListsSongs
            )
            listsRecyclerView!!.adapter = listsAdapter
            listsRecyclerView!!.layoutManager = LinearLayoutManager(this)
        })
        super.onRestart()
    }

    private fun getSummaryLists(success : (Any?) -> Unit){
        cancAPI.loadSummaryLists( success = { listOfLists ->
            listOfListsSongs.clear()
            listOfListsSongs.addAll(listOfLists)
            success(null)
        })
    }
}