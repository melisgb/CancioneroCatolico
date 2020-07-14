package com.example.cancionerocatolico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ListView
import com.google.android.material.chip.Chip

class ViewSongsByKeyActivity : AppCompatActivity() {
    var selectedFilters : String = ""
    var cancAPI = CancioneroAPI()
    var songsTagsAdapter : SongAdapter? = null
    lateinit var songsTagsListView : ListView
    var songsList = ArrayList<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_songs_by_key)

        songsTagsAdapter = SongAdapter(this, songsList)
        songsTagsListView = findViewById<ListView>(R.id.lvListSongsByTags)
        songsTagsListView.adapter = songsTagsAdapter
        songsList.clear()

        getSongByTags(selectedFilters)

        val intentExtra = intent.extras
        val key = intentExtra!!.getString("key")

        if(key == "parts"){
            val horizontalViewSeasons = findViewById<HorizontalScrollView>(R.id.chipsForSeasons)
            horizontalViewSeasons.visibility = View.INVISIBLE
        }
        if(key == "seasons"){
            val horizontalViewParts = findViewById<HorizontalScrollView>(R.id.chipsForParts)
            horizontalViewParts.visibility = View.INVISIBLE
        }

    }

    fun chipClicked(view: View) {
        val chipFilter = findViewById<Chip>(view.id)
        val chipText = chipFilter.text.toString()

        if(selectedFilters.contains(chipText)){
            val idx = selectedFilters.indexOf(chipText)
            selectedFilters = selectedFilters.substring(0,idx) + selectedFilters.substringAfter(chipText)
            selectedFilters = selectedFilters.removeSuffix(",")
            chipFilter.isSelected = false
        }
        else{
            chipFilter.isSelected = true
            if(selectedFilters!=""){
                selectedFilters += ",$chipText"
            }
            else {
                selectedFilters += chipText
            }
        }

        getSongByTags(selectedFilters)
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