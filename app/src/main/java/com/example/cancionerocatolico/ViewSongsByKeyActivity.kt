package com.example.cancionerocatolico

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.example.cancionerocatolico.adapter.SongAdapter
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.Song
import com.example.cancionerocatolico.utils.UserHelper
import com.google.android.material.chip.Chip

class ViewSongsByKeyActivity : AppCompatActivity() {
    val selectedFilters = HashSet<String>()
    var cancAPI = CancioneroAPI({ UserHelper.getUserID(this) })
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

        songsTagsListView.setOnItemClickListener { parent, view, position, longID ->
            val songId = longID.toInt() //calling getItemId
            val intent = Intent(this, ReadSongActivity::class.java)
            intent.putExtra("song_id", songId)
            startActivity(intent)
        }
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