package com.example.cancionerocatolico

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import com.example.cancionerocatolico.adapter.SongAdapter
import com.example.cancionerocatolico.api.CancioneroAPI
import com.example.cancionerocatolico.objects.Song
import com.example.cancionerocatolico.utils.UserHelper
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_read_song.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

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
        songsTagsAdapter = SongAdapter(this, songsList, HashSet<Int>()) //TODO: Fix this
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

    override fun onCreateOptionsMenu( menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.songs_by_key_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        val chipNames = ArrayList<String>()
        chipNames.add("chip_entrance")
        return when (item.itemId) {
            R.id.action_refresh_filters-> {
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
                getSongByTags("%")
                true
            }
            else -> false
        }
    }

}