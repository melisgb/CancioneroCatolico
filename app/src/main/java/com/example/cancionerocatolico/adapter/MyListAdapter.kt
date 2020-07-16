package com.example.cancionerocatolico.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import com.example.cancionerocatolico.objects.ListSongs
import com.example.cancionerocatolico.R
import com.example.cancionerocatolico.ViewMyListsActivity
import com.example.cancionerocatolico.ViewSpecificListActivity
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.utils.UserHelper
import java.lang.StringBuilder

class MyListAdapter(val context: Context, private val listOfLists : ArrayList<ListSongs>) : RecyclerView.Adapter<MyListAdapter.ViewHolder>() {

    var cancAPI = CancioneroAPI({ UserHelper.getUserID(context) })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //to set the view attributes based on the data
        val list : ListSongs = listOfLists[position]
        val listNameTxtView = holder.listNameTxtView
        listNameTxtView.text = list.listSongsName

        val viewgroup = holder.viewg
        viewgroup.setOnClickListener {
            val intent = Intent(context, ViewSpecificListActivity::class.java)
            intent.putExtra("listID", list.listSongsID)
            intent.putExtra("listName", list.listSongsName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        viewgroup.setOnLongClickListener {
            val id = list.listSongsID
            showPopUpMenu(viewgroup, id, list.listSongsName)
            true
        }
    }

    inner class ViewHolder(listItemView : View) : RecyclerView.ViewHolder(listItemView) {
        //The holder will contain/initialize the view that'll be set as a row.
        val listNameTxtView = listItemView.findViewById<TextView>(R.id.txtvListNameElem)
        val viewg = listItemView.findViewById<Group>(R.id.viewgListElem)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun showPopUpMenu(v : View, listID : Int, listName : String){
        val popup = PopupMenu(context, v, Gravity.RIGHT )
        popup.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.act_shareList -> {
                    cancAPI.loadCurrentList(listID,
                        success = { list ->
                            val sharingIntent = Intent(Intent.ACTION_SEND)
                            sharingIntent.setType("text/plain")
                            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Lista Canciones: $listName")

                            val stringOfSongs = StringBuilder()
                            stringOfSongs.append("*Lista Canciones ${listName} \n*")

                            stringOfSongs.append(list.map { song -> song.songID.toString() +": " + song.songTitle }.joinToString("\n"))
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, stringOfSongs.toString())
                            context.startActivity(Intent.createChooser(sharingIntent, "Share via")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            Toast.makeText(context, "Compartiendo lista", Toast.LENGTH_SHORT).show()
                        })
                    true
                }
                R.id.act_deleteList -> {
                    cancAPI.removeWholeList(listID,
                        success = {
                            cancAPI.loadSummaryLists { lists ->
                                listOfLists.clear()
                                listOfLists.addAll(lists)
                                notifyDataSetChanged()
                                Toast.makeText(context, "Lista eliminada", Toast.LENGTH_SHORT).show()
                            }
                    })
                    true
                }
                else ->
                    false
            }
        }
        popup.inflate(R.menu.popup_list_menu)
        popup.setForceShowIcon(true)
        popup.show()
    }

}
