package com.example.cancionerocatolico

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.cancionerocatolico.adapter.SongAdapter
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.Song
import com.example.cancionerocatolico.utils.UserHelper
import kotlinx.android.synthetic.main.activity_view_specific_list.*
import kotlinx.android.synthetic.main.element_view_songs.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class ViewSpecificListActivity : AppCompatActivity() {
    var mySongsList = ArrayList<Song>()
    var songsAdapter : SongAdapter? = null
    var selectedSongs = HashSet<Int>()
    var actionMode : ActionMode? = null
    var listID : Int = 0
    var cancAPI = CancioneroAPI({ UserHelper.getUserID(this) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_specific_list)

        title = getString(R.string.viewSpecificList_title)

        val extras = intent.extras
        listID = extras!!.getInt("listID")
        val listName = extras.getString("listName")
        val currentListNameTxtV = findViewById<TextView>(R.id.txtvMyListName)
        currentListNameTxtV.text = listName

        val editListNameBtn = findViewById<ImageButton>(R.id.btnEditListName)
        if(listName == "Favoritos"){
            editListNameBtn.visibility = View.INVISIBLE
        }
        else{
            editListNameBtn.setOnClickListener {
                editListName(listID)
            }
        }

//        loadSongs() //to load dummydata
        getSongsCurrentList(
            listID,
            success = { currentList ->
                mySongsList.clear()
                mySongsList.addAll(currentList as ArrayList<Song>)
                songsAdapter = SongAdapter(this, mySongsList, HashSet<Int>())
                var songsListView = findViewById<ListView>(R.id.lvSpecificList)
                songsListView.adapter = songsAdapter

                songsListView.setOnItemClickListener { parent, view, position, longID ->
                    val songId = longID.toInt()
                    if(selectedSongs.isEmpty()){
                    val intent = Intent(this, ReadSongActivity::class.java)
                    intent.putExtra("song_id", songId)
                    startActivity(intent)
//                    finish()
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
                songsListView.setOnItemLongClickListener { parent, view, position, longID ->
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
                                actionMode = this@ViewSpecificListActivity.startActionMode(actionModeCallback)
                        }
                    }
                    true
                }
            })
    }

    override fun onRestart() {
        getSongsCurrentList(
            listID,
            success = { currentList ->
                mySongsList.clear()
                mySongsList.addAll(currentList as ArrayList<Song>)
                songsAdapter = SongAdapter(this, mySongsList, HashSet<Int>())
                var songsListView = findViewById<ListView>(R.id.lvSpecificList)
                songsListView.adapter = songsAdapter
            })
        super.onRestart()
    }

    private val actionModeCallback = object : ActionMode.Callback {
        // Called when the action mode is created
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.view_list_actions_menu, menu)
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
                R.id.action_deleteSongFromList -> {
                    cancAPI.removeFromList(listID, selectedSongs.joinToString(","),
                    success = {
                        Toast.makeText(applicationContext, getString(R.string.toast_songs_deleted_fromList), Toast.LENGTH_SHORT).show()
                        getSongsCurrentList(
                            listID,
                            success = { currentList ->
                                mySongsList.clear()
                                mySongsList.addAll(currentList as ArrayList<Song>)
                                refreshAll()
                            })

                    })
                    mode.finish()
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
    fun getSongsCurrentList(listID : Int, success : (Any?) -> Unit) {
        cancAPI.loadCurrentList(
            listID,
            success = { currentList ->
                success(currentList)
        })
    }

    private fun editListName(listID: Int){
        val builder = AlertDialog.Builder(this@ViewSpecificListActivity)
        var newListName : String
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_list_name, null)
        builder.setTitle(R.string.msg_edit_list_name)
        builder.setView(dialogView)
        builder.setIcon(R.drawable.ic_edit_song)
        val newListNameEditTxt = dialogView.findViewById<EditText>(R.id.etxtEditListName)

        cancAPI.loadSummaryLists(
            //get all listSongs summarized from DB
            success = { listOfLists ->
                val currentListSong = listOfLists.find{ l -> l.listSongsID == listID }
                newListNameEditTxt.setText(currentListSong!!.listSongsName)
                newListNameEditTxt.setSelection(newListNameEditTxt.text.length)
                newListName = newListNameEditTxt.text.toString()

                builder.setNegativeButton("Cancelar") { dialog, which ->
                    dialog.cancel()
                }
                builder.setPositiveButton("Guardar") { dialog, which ->
                    newListName = newListNameEditTxt.text.toString()
                    if (newListName.length <= 35) {
                        cancAPI.updateList(listID, newListName,
                            success = {
                                songsAdapter!!.notifyDataSetChanged() //solo es neceario notificar al adapter de nuevo
                            })
                        txtvMyListName.text = newListName
                    } else {
                        Toast.makeText(
                            this@ViewSpecificListActivity,
                            getString(R.string.toast_maxLength_reached),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                val mDialog = builder.create()
                mDialog.show()
            })
    }
}