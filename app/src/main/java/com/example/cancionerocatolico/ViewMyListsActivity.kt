package com.example.cancionerocatolico

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        refreshAdapter()
        super.onRestart()
    }

    override fun onCreateOptionsMenu( menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.view_my_lists_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        return when (item.itemId) {
            R.id.action_import_list -> {
                val dialog = AlertDialog.Builder(this@ViewMyListsActivity)
                dialog.setTitle(R.string.import_dialog_title)
                dialog.setIcon(R.drawable.ic_import_list_normal)

                val view = layoutInflater.inflate(R.layout.dialog_import_list, null)
                dialog.setView(view)
                val newSongsList = ArrayList<Int>()

                dialog.setPositiveButton("Crear lista") { dialog, which ->
                    val importText = view.findViewById<EditText>(R.id.etxtImportListText)
                    val textArr = importText.text.split("\n")
                    val listName = textArr[0].substringAfter("*Lista Canciones").substringBeforeLast("*").trim()
                    for(line in 1 until textArr.size){
                        if(textArr[line] == "") continue
                        newSongsList.add(textArr[line].substringBefore(":").removePrefix("*").toInt())
                    }
                    createImportedList(listName, newSongsList)
                }
                dialog.setNeutralButton("Reemplazar Lista") { dialog, which ->
                    val importText = view.findViewById<EditText>(R.id.etxtImportListText)
                    val textArr = importText.text.split("\n")
                    val listName = textArr[0].substringAfter("*Lista Canciones").substringBeforeLast("*").trim()
                    for(line in 1 until textArr.size){
                        if(textArr[line] == "") continue
                        newSongsList.add(textArr[line].substringBefore(":").removePrefix("*").toInt())
                    }
                    addSongsToList(listName, newSongsList)
                }

                dialog.show()

                true
            }
            else -> false
        }
    }

    private fun getSummaryLists(success : (Any?) -> Unit){
        cancAPI.loadSummaryLists( success = { listOfLists ->
            listOfListsSongs.clear()
            listOfListsSongs.addAll(listOfLists)
            success(null)
        })
    }
    private fun refreshAdapter(){
        getSummaryLists(success = {
            listsAdapter = MyListAdapter(
                applicationContext,
                listOfListsSongs
            )
            listsRecyclerView!!.adapter = listsAdapter
            listsRecyclerView!!.layoutManager = LinearLayoutManager(this)
        })
    }

    private fun createImportedList(impListName : String, newlistOfSongs : ArrayList<Int>){
        //Crea una nueva lista
        //Cuando el nombre de la lista a importar existe, cambia el nombre agregando un (1)
        var listName = impListName
        cancAPI.loadSummaryLists(
            success = { listOfLists ->
                val listID = listOfLists.find { l -> l.listSongsName == listName }?.listSongsID
                if (listID != null) {
                    listName = listName + "(1)"
                }
                //insert new list
                cancAPI.createList(listName,
                    success = { newlistID ->
                        val strImportedSongs = newlistOfSongs.joinToString(",")
                        cancAPI.insertToList(newlistID, strImportedSongs,
                            success = {
                                Toast.makeText(applicationContext,
                                    "Lista creada exitosamente", Toast.LENGTH_SHORT).show()
                                refreshAdapter()
                            })
                    })

            })

    }

    private fun addSongsToList(listName : String, newlistOfSongs : ArrayList<Int>){
        //Crea una nueva lista
        //Cuando el nombre de la lista a importar existe, elimina esa lista y crea una nueva con el mismo nombre
        cancAPI.loadSummaryLists(
            success = { listOfLists  ->
                val listID = listOfLists.find{ l -> l.listSongsName == listName }?.listSongsID
                if(listID == null) {
                    //insert new list
                    cancAPI.createList(listName,
                        success = { newlistID ->
                            val strImportedSongs = newlistOfSongs.joinToString(",")
                            cancAPI.insertToList(newlistID, strImportedSongs,
                                success = {
                                    Toast.makeText(applicationContext,
                                        "Lista reemplazada exitosamente", Toast.LENGTH_SHORT).show()
                                    refreshAdapter()
                                })
                        })
                }
                else {
                    //List already exists, new list will replace old one
                    //TODO: is better to delete the content and maintain the existing listID
                    cancAPI.removeWholeList(listID,
                        success = {
                            cancAPI.createList(listName,
                                success = { newlistID ->
                                    val strImportedSongs = newlistOfSongs.joinToString(",")
                                    cancAPI.insertToList(newlistID, strImportedSongs,
                                        success = {
                                            Toast.makeText(applicationContext,
                                                "Lista reemplazada exitosamente", Toast.LENGTH_SHORT).show()
                                            refreshAdapter()
                                        })
                                })
                        })
                }
            })

    }
}