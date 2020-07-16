package com.example.cancionerocatolico.adapter

import android.app.Activity
import android.view.*
import android.widget.BaseAdapter
import com.example.cancionerocatolico.R
import com.example.cancionerocatolico.objects.Song
import kotlinx.android.synthetic.main.element_view_songs.view.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

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