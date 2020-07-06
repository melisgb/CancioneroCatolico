package com.example.cancionerocatolico

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_read_song.*
import java.net.URLEncoder

class ReadSongActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_song)

        val song_id = intent.extras!!.getInt("song_id")
        loadSong(song_id)
    }

    fun loadSong(songID : Int)  {
        val url = "http://10.0.2.2:8000/cancionero/" +
                "get_songs.php?case=3&song_id=${songID}"
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
            R.id.action_edit_song -> {
                val intent = Intent(applicationContext, EditSongActivity::class.java )
                //TODO: Send songInfo to edit.
                startActivity(intent)
                true
            }
            else -> false
        }


        }
}