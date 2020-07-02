package com.example.cancionerocatolico

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyListAdapter(private val listOfLists : List<ListSongs>) : RecyclerView.Adapter<MyListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListAdapter.ViewHolder {
        //to inflate the item layout and create holder
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val listView = inflater.inflate(R.layout.element_view_my_lists, parent, false)
        return ViewHolder(listView)
    }

    override fun getItemCount(): Int {
        //to determine the number of items
        return listOfLists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //to set the view attributes based on the data
        val list : ListSongs = listOfLists[position]

        val listNameTxtView = holder.listNameTxtView
        listNameTxtView.setText(list.listSongsName)
    }



    inner class ViewHolder(listItemView : View) : RecyclerView.ViewHolder(listItemView) {
        //The holder will contain/initialize the view that'll be set as a row.
        val listNameTxtView = listItemView.findViewById<TextView>(R.id.txtvListNameElem)


    }

}
