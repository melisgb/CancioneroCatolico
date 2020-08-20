package com.example.cancionerocatolico

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
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
    var tags : String = ""
    private var showingFilters = false
    private val selectedFilters = HashSet<String>()
    private var showFiltersItem : MenuItem? = null

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
        getSongs(query, tags)
        selectedFilters.clear()
        setColorOnFiltersIcon()

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

        val refreshFilters = findViewById<ImageView>(R.id.ivRefreshFilters)
        refreshFilters.setOnClickListener {
            val partsChips = findViewById<ViewGroup>(R.id.partsChipGroup)
            val partsChipsList = partsChips.children.toList()
            for(v in partsChipsList){
                val chip = v as Chip
                chip.isSelected = false
                chip.isCloseIconVisible = false
            }
            val seasonsChips = findViewById<ViewGroup>(R.id.seasonsChipGroup)
            val seasonsChipsList = seasonsChips.children.toList()
            for(v in seasonsChipsList){
                val chip = v as Chip
                chip.isSelected = false
                chip.isCloseIconVisible = false
            }
            selectedFilters.clear()
            tags = ""
            setColorOnFiltersIcon()
            getSongs(query, tags)
        }
    }

    override fun onRestart() {
        getSongs(query, tags)
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
                getSongs(query, tags)
                songsListView.adapter = songsAdapter
                return false
            }
        })


        showFiltersItem = menu.findItem(R.id.show_filters)
        showFiltersItem?.setOnMenuItemClickListener{
            val layoutChips = findViewById<LinearLayout>(R.id.layout_chips)

            if(showingFilters){
                layoutChips.visibility = ViewGroup.GONE
                showingFilters = false
            }
            else{
                layoutChips.visibility = ViewGroup.VISIBLE
                showingFilters = true
            }
            true
        }

        return true
    }

    fun getSongs(keyword : String, tags: String, startFrom : Int=0){
        //FROM API
        cancAPI.loadSongs(keyword, tags, startFrom,
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
        tags = selectedFilters.joinToString( "," )
        setColorOnFiltersIcon()
        getSongs(query, tags)
    }

    private fun setColorOnFiltersIcon(){
        if(selectedFilters.isNotEmpty()) {
            var drawable =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_show_filters, null)
            drawable = DrawableCompat.wrap(drawable!!)
            DrawableCompat.setTint(drawable, getColor(R.color.accentColor))
            showFiltersItem?.icon = drawable
        }
        else{
            var drawable =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_show_filters, null)
            drawable = DrawableCompat.wrap(drawable!!)
            DrawableCompat.setTint(drawable, getColor(R.color.menuIconTextColor))
            showFiltersItem?.icon = drawable
        }
    }
}