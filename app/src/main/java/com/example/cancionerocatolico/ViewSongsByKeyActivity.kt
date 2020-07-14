package com.example.cancionerocatolico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ListView
import com.google.android.material.chip.Chip

class ViewSongsByKeyActivity : AppCompatActivity() {
    val selectedFilters = HashSet<String>()
    val cancAPI = CancioneroAPI()
    var songsTagsAdapter : SongAdapter? = null
    lateinit var songsTagsListView : ListView
    var songsList = ArrayList<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_songs_by_key)

        title = getString(R.string.view_songs_by_key_title)
        songsTagsAdapter = SongAdapter(this, songsList)
        songsTagsListView = findViewById<ListView>(R.id.lvListSongsByTags)
        songsTagsListView.adapter = songsTagsAdapter
        songsList.clear()

        getSongByTags("%")
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


    fun getSongByTags(tags : String){
        //FROM API
        cancAPI.loadSongsByTags(tags,
            success = { listOfSongs  ->
                songsList.clear()
                songsList.addAll(listOfSongs as ArrayList<Song>)
                songsTagsAdapter!!.notifyDataSetChanged()
            },
            fail = {
                songsList.clear()
                songsTagsAdapter!!.notifyDataSetChanged()
            })
    }
}