package com.example.cancionerocatolico

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.element_view_songs.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class ViewSongsActivity : AppCompatActivity() {
//    var songsList = ArrayList<Song>()
    var songsAdapter : SongAdapter? = null
    lateinit var songsListView : ListView
    var cancAPI = CancioneroAPI()
    var selectedSongs = HashSet<Int>()
    var actionMode : ActionMode? = null
    var currentList : ListSongs? = null  //For func loadCurrentList

    //    implementation of Songs Action mode - later implement it as class and interface.
    private val actionModeCallback = object : ActionMode.Callback {
        // Called when the action mode is created
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.view_song_actions_menu, menu)
            mode.title = "Elija una opcion"
            return true
        }
        // Called each time the action mode is shown. Always called after onCreateActionMode.
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_addToList -> {
                    cancAPI.loadSummaryLists(
                        //get all listSongs summarized from DB
                        success = { listOfLists  ->
                            var listNamesA = ArrayList<String>()
                            for(list in listOfLists) {
                                listNamesA.add(list.listSongsName)
                            }
                            //to create New list
                            val df = SimpleDateFormat("yy_MM_dd_HH_mm_ss")
                            val currDate = Date()
                            listNamesA.add("NuevaLista"+ df.format(currDate))

                            val listNames = listNamesA.toArray(emptyArray<String>())
                            var selected_ListName = ""
                            val copySelectedSongs = HashSet<Int>(selectedSongs)
                            val builder = AlertDialog.Builder(this@ViewSongsActivity) /*ViewSongsActivity*/
                            builder.setTitle(R.string.choose_list)
                            builder.setIcon(R.drawable.ic_add_to_list)
                            builder.setSingleChoiceItems(listNames, -1)  { dialogInterface, i ->
                                selected_ListName = listNames[i]

                                var myListSongs = HashMap<Int, Song>()
                                for(songID in copySelectedSongs) {
                                    myListSongs[songID] = songsList[songID]
                                }
                                var listID = listOfLists.find{ l -> l.listSongsName == selected_ListName }?.listSongsID
//
                                if(listID == null){
                                    //insert new list
                                    cancAPI.createList(selected_ListName,
                                        success = { newlistID ->
                                            listID = newlistID
                                            cancAPI.loadCurrentList(listID!!,
                                                success = { currentList ->
                                                    var strSelectedSongs =
                                                        copySelectedSongs.joinToString(",")
                                                    cancAPI.insertToList(listID!!, strSelectedSongs)
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Canciones agregadas a ${selected_ListName}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                })
                                        })
                                }
                                else {
                                    //   When there is a LISTID / the list is already created
                                    cancAPI.loadCurrentList(listID!!,
                                        success = { currentList ->
                                            var strSelectedSongs =
                                                copySelectedSongs.joinToString(",")
                                            cancAPI.insertToList(listID!!, strSelectedSongs)
                                            Toast.makeText(
                                                applicationContext,
                                                "Canciones agregadas a ${selected_ListName}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        })
                                }
                                refreshAll()
                                dialogInterface.dismiss()
                            }
                            builder.setNeutralButton("Cancelar") { dialog, which ->
                                dialog.cancel()
                            }
                            val mDialog = builder.create()
                            mDialog.show()
                            mode.finish()
                        }
                    )
                    true
                }
                R.id.action_addToFavs -> {
                    cancAPI.loadSummaryLists(
                        //get all listSongs summarized from DB
                        success = { listOfLists ->
                            val copySelectedSongs = HashSet<Int>(selectedSongs)
                            var selected_ListName = "Favoritos"
                            val listID = listOfLists.find{ l -> l.listSongsName == selected_ListName }?.listSongsID
                            //   When there is a LISTID / the list is already created
                            cancAPI.loadCurrentList(listID!!,
                                success = { currentList ->
                                    var strSelectedSongs =
                                        copySelectedSongs.joinToString(",")
                                    cancAPI.insertToList(listID!!, strSelectedSongs)
                                    Toast.makeText(
                                        applicationContext,
                                        "Canciones agregadas a ${selected_ListName}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            refreshAll()
                            mode.finish() // Action picked, so close the CAB
                        })
                    true
                }
                else ->
                    false
            }
        }
        // Called when the user exits the action mode
        override fun onDestroyActionMode(mode: ActionMode){
            actionMode = null
            refreshAll()
        }
    }

    fun refreshAll() {
        selectedSongs.clear()
        songsAdapter?.notifyDataSetChanged()
    }


    companion object{
        var songsList = ArrayList<Song>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_songs)

        songsAdapter = SongAdapter(this, songsList)
        songsListView = findViewById<ListView>(R.id.lvListofSongs)
        songsListView.adapter = songsAdapter


        songsList.clear()
//        cancioneroAPI.loadSongs()
        getSongs("%", 0)

        val btn_FloatingAction = findViewById<FloatingActionButton>(R.id.btnFloatingAction)
        btn_FloatingAction.setOnClickListener {
            val intent = Intent(this, EditSongActivity::class.java)
            startActivity(intent)
        }

        songsListView.setOnItemClickListener {parent, view, position, longID ->
            val songId = longID.toInt() //calling getItemId
            if(selectedSongs.isEmpty()) {
                val intent = Intent(this, ReadSongActivity::class.java)
                intent.putExtra("song_id", songId)
                startActivity(intent)
            }
            else if(selectedSongs.contains(songId)){
                selectedSongs.remove(songId)
                val checkbox = view.findViewById<CheckBox>(chkboxSongElem.id)
                checkbox.isChecked = false
                checkbox.visibility = View.INVISIBLE

                if(selectedSongs.isEmpty()) {
                    actionMode?.finish()
                }
            }
            else if(!selectedSongs.contains(songId)){
                selectedSongs.add(songId)
                val checkbox = view.findViewById<CheckBox>(chkboxSongElem.id)
                checkbox.isChecked = true
                checkbox.visibility = View.VISIBLE
            }
        }

        songsListView.setOnItemLongClickListener {parent, view, position, longID ->
            val songId = longID.toInt()
            if(selectedSongs.contains(songId)) {
                selectedSongs.remove(songId)
                val checkbox = view.findViewById<CheckBox>(chkboxSongElem.id)
                checkbox.isChecked = false
                checkbox.visibility = View.INVISIBLE
            }
            else if(!selectedSongs.contains(songId)){
                selectedSongs.add(songId)
                val checkbox = view.findViewById<CheckBox>(chkboxSongElem.id)
                checkbox.isChecked = true
                checkbox.visibility = View.VISIBLE
            }
            if(!selectedSongs.isEmpty()){
                when(actionMode){
                    null ->
                        actionMode = this.startActionMode(actionModeCallback)
                }
            }
            true
        }

    }

    override fun onResume() {
        getSongs("%",0)
        super.onResume()
    }

    // SEARCH_BAR IMPLEMENTATION
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search_bar)
        val searchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.queryHint = "Buscar palabra clave..."
        searchView.maxWidth = Int.MAX_VALUE

        searchItem.setOnMenuItemClickListener {
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@ViewSongsActivity, "Buscando $query", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                //TODO: Search Songs in DB
                getSongs(query!!, 0)
                songsListView.adapter = songsAdapter
                return false
            }
        })

        //val keyword_extra: String? = this.intent.getStringExtra(EXTRA_KEYWORD)
        val keyword_extra: String? = ""
        if (!keyword_extra.isNullOrEmpty()) {
            //TODO: Search Songs in DB
            getSongs(keyword_extra, 0)
            songsListView.adapter = songsAdapter

        }
        return true
    }


    fun getSongs(keyword : String, startFrom : Int){
        //FROM API
        cancAPI.loadSongs(keyword, startFrom,
            success = { listOfSongs  ->
                songsList.clear()
                songsList.addAll(listOfSongs as ArrayList<Song>)
                songsAdapter!!.notifyDataSetChanged()
            },
            fail = {
                songsList.clear()
                songsAdapter!!.notifyDataSetChanged()
            })
    }








}