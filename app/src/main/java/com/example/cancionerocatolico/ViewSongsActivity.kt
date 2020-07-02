package com.example.cancionerocatolico

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView

class ViewSongsActivity : AppCompatActivity() {
    var songsList = ArrayList<Song>()
    var songsAdapter : SongAdapter? = null
    lateinit var songsListView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_songs)


        songsAdapter = SongAdapter(this, songsList)
        songsListView = findViewById<ListView>(R.id.lvListofSongs)
        songsListView.adapter = songsAdapter

        songsList.clear()
        loadSongs()

    }

    // SEARCH_BAR IMPLEMENTATION
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search_bar)
        val searchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.queryHint = "Buscar..."
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
                //val listSongs1 = db.searchSongs(query!!)
                //val adapter = ListSongsAdapter(this@ViewSongsActivity, listSongs1, selected_Set)
                songsListView.adapter = songsAdapter
                return false
            }
        })

        //val keyword_extra: String? = this.intent.getStringExtra(EXTRA_KEYWORD)
        val keyword_extra: String? = ""
        if (!keyword_extra.isNullOrEmpty()) {
            //TODO: Search Songs in DB
            //val listSongs1 = db.searchSongsByTags(keyword_extra)
            //val adapter = SongAdapter(this@ViewSongsActivity, listSongs1, selected_Set)
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
}