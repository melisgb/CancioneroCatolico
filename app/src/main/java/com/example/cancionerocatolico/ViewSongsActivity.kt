package com.example.cancionerocatolico

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.example.cancionerocatolico.adapter.SongAdapter
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.Song
import com.example.cancionerocatolico.utils.UserHelper
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.element_view_songs.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class ViewSongsActivity : AppCompatActivity() {
    var songsAdapter : SongAdapter? = null
    lateinit var songsListView : ListView
    var cancAPI = CancioneroAPI({ UserHelper.getUserID(this) })
    var selectedSongs = HashSet<Int>()
    var actionMode : ActionMode? = null
    var query : String = "%"
    var showingFilters = false
    private val selectedFilters = HashSet<String>()

    //    implementation of Songs Action mode - later implement it as class and interface.
    private val actionModeCallback = object : ActionMode.Callback {
        // Called when the action mode is created
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.songs_actions_menu, menu)
            mode.title = getString(R.string.choose_option)
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
                            val listNamesA = ArrayList<String>()
                            for(list in listOfLists) {
                                listNamesA.add(list.listSongsName)
                            }
                            //to create New list
                            val df = SimpleDateFormat("dd_MM_yy_HHmmss")
                            val currDate = Date()
                            listNamesA.add("Lista"+ df.format(currDate))

                            val listNames = listNamesA.toArray(emptyArray<String>())
                            var selectedListName = ""
                            val copySelectedSongs = HashSet<Int>(selectedSongs)
                            val builder = AlertDialog.Builder(this@ViewSongsActivity) /*ViewSongsActivity*/
                            builder.setTitle(R.string.choose_list)
                            builder.setIcon(R.drawable.ic_add_to_list_black)
                            builder.setSingleChoiceItems(listNames, -1)  { dialogInterface, i ->
                                selectedListName = listNames[i]

                                var listID = listOfLists.find{ l -> l.listSongsName == selectedListName }?.listSongsID
                                if(listID == null){
                                    //insert new list
                                    cancAPI.createList(selectedListName,
                                        success = { newlistID ->
                                            listID = newlistID
                                            val songsQty = copySelectedSongs.size
                                            val strSelectedSongs = copySelectedSongs.joinToString(",")
                                            cancAPI.insertToList(listID!!, strSelectedSongs,
                                                success = {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        resources.getQuantityString(R.plurals.toast_songs_added_toList, songsQty, selectedListName),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                })

                                        })
                                } else {
                                    //   insert to already created list
                                    val songsQty = copySelectedSongs.size
                                    val strSelectedSongs = copySelectedSongs.joinToString(",")
                                    cancAPI.insertToList(listID!!, strSelectedSongs,
                                        success = {
                                            Toast.makeText(applicationContext,resources.getQuantityString(R.plurals.toast_songs_added_toList, songsQty, selectedListName),Toast.LENGTH_SHORT).show()
                                        })
                                }
                                refreshAll()
                                dialogInterface.dismiss()
                            }
                            builder.setNeutralButton(getString(R.string.cancel_dialog)) { dialog, _ ->
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
                            val songsQty = copySelectedSongs.size
                            val selectedListName = "Favoritos"
                            val listID = listOfLists.find{ l -> l.listSongsName == selectedListName }?.listSongsID
                            if(listID == null){
                                cancAPI.createList(selectedListName,
                                    success = {newListID ->
                                        val strSelectedSongs =
                                            copySelectedSongs.joinToString(",")
                                        cancAPI.insertToList(newListID, strSelectedSongs,
                                            success = {
                                                Toast.makeText(applicationContext,resources.getQuantityString(R.plurals.toast_songs_added_toList, songsQty, selectedListName),Toast.LENGTH_SHORT).show()
                                            })

                                    } )
                            }
                            else {
                                //   When there is a ListID
                                val strSelectedSongs = copySelectedSongs.joinToString(",")
                                cancAPI.insertToList(listID, strSelectedSongs,
                                    success = {
                                        Toast.makeText(applicationContext,resources.getQuantityString(R.plurals.toast_songs_added_toList, songsQty, selectedListName),Toast.LENGTH_SHORT).show()
                                    })
                            }
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

        title = getString(R.string.viewSongs_title)
        songsAdapter =
            SongAdapter(this, songsList, selectedSongs)
        songsListView = findViewById<ListView>(R.id.lvListofSongs)
        songsListView.adapter = songsAdapter

        songsList.clear()
        getSongs(query, 0)

        val fabAddSong = findViewById<FloatingActionButton>(R.id.fabAddSong)
        fabAddSong.isVisible = UserHelper.getUserID(this)==1 || UserHelper.getUserID(this)==2
        fabAddSong.setOnClickListener {
            val intent = Intent(this, EditSongActivity::class.java)
            startActivity(intent)
        }

        songsListView.setOnItemClickListener {_, view, _, longID ->
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

        songsListView.setOnItemLongClickListener {_, view, _, longID ->
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
            if(selectedSongs.isNotEmpty()){
                when(actionMode){
                    null ->
                        actionMode = this@ViewSongsActivity.startActionMode(actionModeCallback)
                }
            }
            true
        }
    }

    override fun onRestart() {
        getSongs(query,0)
        super.onRestart()
    }

    // SEARCH_BAR IMPLEMENTATION
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.view_songs_menu, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search_bar)
        val searchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.queryHint = getString(R.string.hint_search_bar)
        searchView.maxWidth = Int.MAX_VALUE

        searchItem.setOnMenuItemClickListener {
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(querytxt: String?): Boolean {
                Toast.makeText(this@ViewSongsActivity, getString(R.string.toast_searching_by, querytxt), Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onQueryTextChange(querytxt: String?): Boolean {
                query = if(querytxt == null) "%" else querytxt
                getSongs(query, 0)
                songsListView.adapter = songsAdapter
                return false
            }
        })


        val showFiltersItem = menu?.findItem(R.id.show_filters)
        showFiltersItem.setOnMenuItemClickListener{
            val layout_chips = findViewById<LinearLayout>(R.id.layout_chips)

            if(showingFilters){
                layout_chips.visibility = ViewGroup.INVISIBLE
                showingFilters = false
            }
            else{
                layout_chips.visibility = ViewGroup.VISIBLE
                showingFilters = true
            }
            true
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
    fun chipClicked(view: View) {
        val chipFilter = findViewById<Chip>(view.id)
        val chipText = chipFilter.text.toString()

        if(selectedFilters.contains(chipText)){
            selectedFilters.remove(chipText)
            chipFilter.isSelected = false
            chipFilter.isCloseIconVisible = false
        }
        else{
            chipFilter.isSelected = true
            chipFilter.isCloseIconVisible = true
            chipFilter.setOnCloseIconClickListener { chipClicked(view) }
            selectedFilters.add(chipText)
        }
        getSongByTags(selectedFilters.joinToString( "," ))
    }
    private fun getSongByTags(tags : String){
        //FROM API
        cancAPI.loadSongsByTags(tags,
            success = { listOfSongs  ->
                songsList.clear()
                songsList.addAll(listOfSongs)
                songsAdapter!!.notifyDataSetChanged()
            },
            fail = {
                songsList.clear()
                songsAdapter!!.notifyDataSetChanged()
            })
    }
}