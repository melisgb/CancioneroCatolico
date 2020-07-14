package com.example.cancionerocatolico

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_view_specific_list.*

class ViewSpecificListActivity : AppCompatActivity() {
    var mySongsList = ArrayList<Song>()
    var songsAdapter : SongAdapter? = null
    var cancAPI = CancioneroAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_specific_list)

        title = getString(R.string.view_specific_list_title)

        val extras = intent.extras
        val listID = extras!!.getInt("listID")
        val listName = extras!!.getString("listName")
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
                songsAdapter = SongAdapter(this, mySongsList)
                var songsListView = findViewById<ListView>(R.id.lvSpecificList)
                songsListView.adapter = songsAdapter

                songsListView.setOnItemClickListener { parent, view, position, longID ->
                    val songId = longID.toInt()
                    val intent = Intent(this, ReadSongActivity::class.java)
                    intent.putExtra("song_id", songId)
                    startActivity(intent)
//                    finish()
                }
            })
    }


    fun loadSongs(){
        /*dummy data*/
        mySongsList.add(
            Song(8, "Carlos' Love", "Entrenados", "Ula ula ula ula", "Entrada, Salmos"))
        mySongsList.add(
            Song(9, "Melingo's Love", "Entrenados", "Ula ula ula ula", "Paz, Salmos"))
        mySongsList.add(
            Song(10, "Loving him", "Entrenados", "Ula ula ula ula", "Paz, Salmos"))

    }

    fun getSongsCurrentList(listID : Int, success : (Any?) -> Unit) {
        cancAPI.loadCurrentList(
            listID,
            success = { currentList ->
                success(currentList)
        })
    }

    fun editListName(listID: Int){
        val builder = AlertDialog.Builder(this@ViewSpecificListActivity)
        var newListName : String
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_list_name, null)
        builder.setTitle(R.string.msg_edit_list_name)
        builder.setView(dialogView)
        builder.setIcon(R.drawable.ic_edit_song)
        var newListNameEditTxt = dialogView.findViewById<EditText>(R.id.etxtEditListName)

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
                                songsAdapter!!.notifyDataSetChanged() //verificar si solo es neceario notificar o si pasar todo la lista al adapter de nuevo
                            })
                        txtvMyListName.text = newListName
//                        refreshAll()
                    } else {
                        Toast.makeText(
                            this@ViewSpecificListActivity,
                            "Longitud no  permitida",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                val mDialog = builder.create()
                mDialog.show()
            })
    }
}