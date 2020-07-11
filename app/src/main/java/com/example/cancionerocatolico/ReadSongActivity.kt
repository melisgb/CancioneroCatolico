package com.example.cancionerocatolico

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_read_song.*
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class ReadSongActivity : AppCompatActivity() {
    var song_id : Int = 0
    var cancAPI = CancioneroAPI()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_song)

        song_id = intent.extras!!.getInt("song_id")
        loadSong(song_id)
    }

    override fun onResume() {
        loadSong(song_id)
        super.onResume()
    }

    fun loadSong(songID : Int)  {

        val url = Uri.parse("http://10.0.2.2:8000/cancionero/get_songs.php?")
            .buildUpon()
            .appendQueryParameter("case", "3")
            .appendQueryParameter("song_id", songID.toString())
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
//                Toast.makeText(applicationContext, "Retrieving posts failed", Toast.LENGTH_SHORT).show()
                //TODO: Define behaviour when returns null
            },
            onSuccess = { listOfPosts ->
//                Toast.makeText(applicationContext, "Loading posts", Toast.LENGTH_SHORT).show()
                val results = listOfPosts as ArrayList<Song>
                val songA = results[0]
                txtvReadSongTitle.setText(songA.songTitle)
                txtvReadSongArtist.setText(songA.songArtist)
                txtvReadSongLyrics.setText(songA.songLyrics)
                txtvReadSongTags.setText(songA.songTags)
                //TODO: Bring info about Favorites
            }
        ).execute(url)
    }



    override fun onCreateOptionsMenu( menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.read_song_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        return when (item.itemId) {
            R.id.action_addSongToList -> {
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
                        val builder = AlertDialog.Builder(this@ReadSongActivity)
                        builder.setTitle(R.string.choose_list)
                        builder.setIcon(R.drawable.ic_add_to_list)
                        builder.setSingleChoiceItems(listNames, -1)  { dialogInterface, i ->
                            selected_ListName = listNames[i]

                            var myListSongs = HashMap<Int, Song>()
                            myListSongs[song_id] = ViewSongsActivity.songsList[song_id]

                            var listID = listOfLists.find{ l -> l.listSongsName == selected_ListName }?.listSongsID
//
                            if(listID == null){
                                //insert new list
                                cancAPI.createList(selected_ListName,
                                    success = { newlistID ->
                                        listID = newlistID
                                        cancAPI.loadCurrentList(listID!!,
                                            success = { currentList ->
                                                cancAPI.insertToList(listID!!, song_id.toString())
                                                Toast.makeText(applicationContext,"Canciones agregadas a ${selected_ListName}",Toast.LENGTH_SHORT).show()
                                            })
                                    })
                            }
                            else {
                                //   When there is a LISTID / the list is already created
                                cancAPI.loadCurrentList(listID!!,
                                    success = { currentList ->
                                        cancAPI.insertToList(listID!!, song_id.toString())
                                        Toast.makeText(applicationContext,"Canciones agregadas a ${selected_ListName}",Toast.LENGTH_SHORT
                                        ).show()
                                    })
                            }
                            dialogInterface.dismiss()
                        }
                        builder.setNeutralButton("Cancelar") { dialog, which ->
                            dialog.cancel()
                        }
                        val mDialog = builder.create()
                        mDialog.show()
                    }
                )
                true
            }
            R.id.action_addSongToFavs -> {
                cancAPI.loadSummaryLists(
                    //get all listSongs summarized from DB
                    success = { listOfLists ->
                        var selected_ListName = "Favoritos"
                        val listID = listOfLists.find{ l -> l.listSongsName == selected_ListName }?.listSongsID
                        //   When there is a LISTID / the list is already created
                        cancAPI.loadCurrentList(listID!!,
                            success = { currentList ->
                                cancAPI.insertToList(listID!!, song_id.toString())
                                Toast.makeText(applicationContext,"Canciones agregadas a ${selected_ListName}",Toast.LENGTH_SHORT).show()
                            })
                    })
                true
            }
            R.id.action_editSong -> {
                val intent = Intent(applicationContext, EditSongActivity::class.java )
                intent.putExtra("songId", song_id)
                intent.putExtra("songTitle", txtvReadSongTitle.text.toString())
                intent.putExtra("songArtist", txtvReadSongArtist.text.toString())
                intent.putExtra("songLyrics", txtvReadSongLyrics.text.toString())
                intent.putExtra("songTags", txtvReadSongTags.text.toString())
                startActivity(intent)
                true
            }
            R.id.action_deleteSong -> {
                cancAPI.deleteSong(
                    song_id,
                    success = {
                        Toast.makeText( applicationContext,"Cancion eliminada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
                true
            }
            else -> false
        }
    }
}