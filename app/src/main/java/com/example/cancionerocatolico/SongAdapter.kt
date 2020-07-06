package com.example.cancionerocatolico

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.element_view_songs.view.*

class SongAdapter(val context: Activity, val songsList: ArrayList<Song>) : BaseAdapter(){
    val layoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //TODO: Implement the layout to inflate and behaviour
        var currentSong = songsList[position]

        val myElementView = layoutInflater.inflate(R.layout.element_view_songs, null)

        myElementView.txtvSongTitleElem.text = currentSong.songTitle
        myElementView.txtvSongArtistElem.text = currentSong.songArtist
        myElementView.txtvSongTagsElem.text = currentSong.songTags

        myElementView.setOnClickListener {
            val intent = Intent(context, ReadSongActivity::class.java)
            intent.putExtra("song_id", currentSong.songID)
            context.startActivity(intent)
        }

        return myElementView
    }

    override fun getItem(position: Int): Any {
        return songsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return songsList.size
    }


}