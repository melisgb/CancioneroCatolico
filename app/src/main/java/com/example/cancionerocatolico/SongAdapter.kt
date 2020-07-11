package com.example.cancionerocatolico

import android.app.Activity
import android.content.Intent
import android.view.*
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.element_view_songs.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.random.Random

class SongAdapter(val context: Activity, val songsList: ArrayList<Song>) : BaseAdapter(){
    val layoutInflater = LayoutInflater.from(context)
    var selectedSongs = HashSet<Int>()
    var actionMode : ActionMode? = null



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //TODO: Implement the layout to inflate and behaviour
        var currentSong = songsList[position]

        val myElementView = layoutInflater.inflate(R.layout.element_view_songs, null)

        myElementView.txtvSongTitleElem.text = currentSong.songTitle
        myElementView.txtvSongArtistElem.text = currentSong.songArtist
        myElementView.txtvSongTagsElem.text = currentSong.songTags

        if(selectedSongs.contains(currentSong.songID)){
            myElementView.chkboxSongElem.visibility = View.VISIBLE
            myElementView.chkboxSongElem.isChecked = true
        }

        return myElementView
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun getItem(position: Int): Any {
        return songsList[position]
    }

    override fun getItemId(position: Int): Long {
        return songsList[position].songID.toLong()
    }

    override fun getCount(): Int {
        return songsList.size
    }



}