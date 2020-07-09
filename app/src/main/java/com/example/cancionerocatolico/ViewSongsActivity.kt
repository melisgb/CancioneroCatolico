package com.example.cancionerocatolico

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.element_view_songs.*
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class ViewSongsActivity : AppCompatActivity() {
//    var songsList = ArrayList<Song>()
    var songsAdapter : SongAdapter? = null
    lateinit var songsListView : ListView
    var selectedSongs = HashSet<Int>()
    var actionMode : ActionMode? = null



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
                    //TODO get all listSongs from DB and save into summaryList
                    val summaryList = ArrayList<ListSongs>()
                    var listNamesA = ArrayList<String>()
                    for(list in summaryList) {
                        listNamesA.add(list.listSongsName)
                    }
                    if(!listNamesA.contains("Nueva Lista")) {
                        listNamesA.add("Nueva Lista")
                    }
                    else {
                        val df = SimpleDateFormat("dd/MM/yy HH:mm:ss")
                        val currDate = Date()
                        listNamesA.add("Nueva Lista"+ df.format(currDate))
                    }
                    val listNames = listNamesA.toArray(emptyArray<String>())

                    var selected_ListName = ""
                    val copySelectedSongs = HashSet<Int>(selectedSongs)
                    val builder = AlertDialog.Builder(applicationContext) /*ViewSongsActivity*/
                    builder.setTitle(R.string.choose_list)
                    builder.setIcon(R.drawable.ic_add_list)
                    builder.setSingleChoiceItems(listNames, -1)  { dialogInterface, i ->
                        selected_ListName = listNames[i]

                        var myListSongs = HashMap<Int, Song>()
                        for(songID in copySelectedSongs) {
//                            myListSongs[songID] = db.getSong(songID)!!
                            myListSongs[songID] = Song(1, "Un padre como el nuestro", "Entrenados", "Ula ula ula ula", "Entrada, Salmos")
                        }
//                        val listID = db.searchSongsListByName(selected_ListName)
//
//                        if(listID > 0) {
//                            //   verify before updating/adding in Favoritos SongsList
//                            val oldSongsList = db.getSongsList(listID)
//                            for(songID in copySelectedSongs){
//                                if(!oldSongsList.songs.containsKey(songID)){
//                                    oldSongsList.songs[songID] = db.getSong(songID)!!
//                                }
//                                else{
//                                    print("${songID} already exists")
//                                }
//                            }
//                            db.updateSongsList(ListSongs(listID, selected_ListName, oldSongsList.songs))
//                            Toast.makeText(this@SearchSongsActivity, "Canciones agregadas a '${selected_ListName}'", Toast.LENGTH_SHORT).show()
//                        }
//                        else {
//                            db.addSongsList(selected_ListName, myListSongs.values.toList())
//                            Toast.makeText(this@SearchSongsActivity, "Canciones agregadas a '${selected_ListName}'", Toast.LENGTH_SHORT).show()
//                        }
//                        refreshAll()
//                        dialogInterface.dismiss()
                    }

                    builder.setNeutralButton("Cancelar") { dialog, which ->
                        dialog.cancel()
                    }
                    val mDialog = builder.create()
                    mDialog.show()
                    mode.finish()
                    true
                }
                R.id.action_addToFavs -> {
//                    val songsPerList = HashMap<Int, Song>()
//
//                    for (songID in selectedSongs) {
//                        songsPerList[songID] = db.getSong(songID)!!
//                    }
//                    val favSongsListID = db.searchSongsListByName("Favoritos")
//
//                    if(favSongsListID > 0) {
//                        //   verify before updating/adding in Favoritos SongsList
//                        val oldFavsList = db.getSongsList(favSongsListID)
//                        for(songID in selectedSongs){
//                            if(!oldFavsList.songs.containsKey(songID)){
//                                oldFavsList.songs[songID] = db.getSong(songID)!!
//                            }
//                            else{
//                                print("${songID} already exists")
//                            }
//                        }
//                        db.updateSongsList(ListSongs(favSongsListID, "Favoritos", oldFavsList.songs))
//                        Toast.makeText( context, "Canciones agregadas a 'Favoritos'", Toast.LENGTH_SHORT).show()
//                    }
//                    else {
//                        db.addSongsList("Favoritos", songsPerList.values.toList())
//                        Toast.makeText(context, "Canciones agregadas a 'Favoritos'", Toast.LENGTH_SHORT).show()
//                    }
//                    refreshAll()
                    mode.finish() // Action picked, so close the CAB
                    true
                }
                R.id.action_deleteSong -> {
//                    for(song in selectedSongs){
//                        //Functionality to check if the song can be deleted (if doesnot belong to a list)
//                        try{
//                            db.deleteSong(db.getSong(song)!!)
//                        }
//                        catch(e: IllegalStateException){
//                            showImpossibleSongDeletion()
//                            break
//                        }
//                    }
//                    adapter?.listSong = db.getSongs()
//                    refreshAll()
//                    mode.finish() // Action picked, so close the CAB
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
//        loadSongs()
        loadSongs("%", 0)

        val btn_FloatingAction = findViewById<FloatingActionButton>(R.id.btnFloatingAction)
        btn_FloatingAction.setOnClickListener {
            val intent = Intent(this, EditSongActivity::class.java)
            startActivity(intent)
        }

        songsListView.setOnItemClickListener {parent, view, position, longID ->
            val songId = longID.toInt()
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
        loadSongs("%",0)
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
                loadSongs(query!!, 0)
                songsListView.adapter = songsAdapter
                return false
            }
        })

        //val keyword_extra: String? = this.intent.getStringExtra(EXTRA_KEYWORD)
        val keyword_extra: String? = ""
        if (!keyword_extra.isNullOrEmpty()) {
            //TODO: Search Songs in DB
            loadSongs(keyword_extra, 0)
            songsListView.adapter = songsAdapter

        }
        return true
    }




    fun loadSongs(){
        /*dummy data*/
        songsList.add(
            Song(1, "Un padre como el nuestro", "Entrenados", "Ula ula ula ula", "Entrada, Salmos"))
        songsList.add(
            Song(2, "Solamente una vez", "Entrenados", "Ula ula ula ula", "Paz, Salmos"))
        songsList.add(
            Song(3, "Una vez nada mas", "Entrenados", "Ula ula ula ula", "Paz, Salmos"))

    }

    fun loadSongs(keyword : String, startFrom : Int){
        //Search into DB based on keyword and updates the List
        val content = URLEncoder.encode(keyword, "utf-8")
        val url = "http://10.0.2.2:8000/cancionero/" +
                "get_songs.php?case=1&keyword=${keyword}&startFrom=${startFrom}"
        MyAsyncTask(
            onFail = {
//                Toast.makeText(applicationContext, "Retrieving posts failed", Toast.LENGTH_SHORT).show()
                songsList.clear()
                songsAdapter!!.notifyDataSetChanged()
            },
            onSuccess = { listOfPosts ->
//                Toast.makeText(applicationContext, "Loading posts", Toast.LENGTH_SHORT).show()
                songsList.clear()
                songsList.addAll(listOfPosts as ArrayList<Song>)
                songsAdapter!!.notifyDataSetChanged()
            }
        ).execute(url)
    }











}