package com.example.cancionerocatolico

import android.net.Uri
import kotlinx.android.synthetic.main.activity_read_song.*
import java.net.URLEncoder

open class CancioneroAPI {

    /***************************************************                SONGS                     ******************************************************************/
    fun loadSongs(){
        /*dummy data*/
        ViewSongsActivity.songsList.add(
            Song(1, "Un padre como el nuestro", "Entrenados", "Ula ula ula ula", "Entrada, Salmos"))
        ViewSongsActivity.songsList.add(
            Song(2, "Solamente una vez", "Entrenados", "Ula ula ula ula", "Paz, Salmos"))
        ViewSongsActivity.songsList.add(
            Song(3, "Una vez nada mas", "Entrenados", "Ula ula ula ula", "Paz, Salmos"))
    }

    fun loadSongs(keyword : String, startFrom : Int, success : (ArrayList<Song>) -> Unit, fail : (Any?) -> Unit){
        //Search into DB based on keyword and updates the List
        val content = URLEncoder.encode(keyword, "utf-8")
        val url = "http://10.0.2.2:8000/cancionero/" +
                "get_songs.php?case=1&keyword=${keyword}&startFrom=${startFrom}"
        MyAsyncTask(
            onFail = {
//                Toast.makeText(applicationContext, "Retrieving posts failed", Toast.LENGTH_SHORT).show()
                fail(null)
            },
            onSuccess = { listOfSongs ->
//                Toast.makeText(applicationContext, "Loading posts", Toast.LENGTH_SHORT).show()
                success(listOfSongs as ArrayList<Song>)
            }
        ).execute(url)
    }

    /***                SONGS EDITION            ***/

    fun addSong(song: Song, success: (Song) -> Unit) {
        //Update a song
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/edit_song.php?")
            .buildUpon()
            .appendQueryParameter("case", "1")
            .appendQueryParameter("song_title", song.songTitle)
            .appendQueryParameter("song_artist", song.songArtist)
            .appendQueryParameter("song_lyrics", song.songLyrics)
            .appendQueryParameter("song_tags", song.songTags)
            .build()
            .toString()

        MyAsyncTask(
            onFail = {  },
            onSuccess = {song ->
                success(song as Song)
            }
        ).execute(url)
    }

    fun readSong(songID: Int, success: (Song) -> Unit) {
        //Reads a song
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/get_songs.php?")
            .buildUpon()
            .appendQueryParameter("case", "3")
            .appendQueryParameter("song_id", songID.toString())
            .build()
            .toString()

        MyAsyncTask(
            onFail = {  },
            onSuccess = {listOfSongs ->
                val songs = listOfSongs as ArrayList<Song>
                success(songs[0])
            }
        ).execute(url)


        //TODO: Bring info about Favorites
    }

    fun updateSong(song: Song, success: (Any?) -> Unit ) {
        //Update a song
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/edit_song.php?")
            .buildUpon()
            .appendQueryParameter("case", "2")
            .appendQueryParameter("song_id", song.songID.toString())
            .appendQueryParameter("song_title", song.songTitle)
            .appendQueryParameter("song_artist", song.songArtist)
            .appendQueryParameter("song_lyrics", song.songLyrics)
            .appendQueryParameter("song_tags", song.songTags)
            .build()
            .toString()

        MyAsyncTask(
            onFail = {  },
            onSuccess = {songID ->
                success(null)
            }
        ).execute(url)
    }

    fun deleteSong(songID: Int, success: (Any?) -> Unit ) {
        //Deletes a song
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/edit_song.php?")
            .buildUpon()
            .appendQueryParameter("case", "3")
            .appendQueryParameter("song_id", songID.toString())
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
            },
            onSuccess = {
                success(null)
            }
        ).execute(url)
    }

    /***************************************************                LISTS                     ******************************************************************/

    fun loadSummaryLists(success : (ArrayList<ListSongs>) -> Unit) {
        //Search in DB all the lists only with name and ID.
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/get_listsongs.php?")
            .buildUpon()
            .appendQueryParameter("user_id", "1")
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
//                Toast.makeText(applicationContext, "Retrieving songs failed", Toast.LENGTH_SHORT).show()
            },
            onSuccess = { listsongs ->
//                Toast.makeText(applicationContext, "Loading songs", Toast.LENGTH_SHORT).show()
                success(listsongs as ArrayList<ListSongs>)
            }
        ).execute(url)

    }

    fun loadCurrentList(listID : Int, success : (ArrayList<Song>) -> Unit) {
        //Search into DB the specific songsList
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/get_songs.php?")
            .buildUpon()
            .appendQueryParameter("case", "2")
            .appendQueryParameter("listsong_id", listID.toString())
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
//                Toast.makeText(applicationContext, "Retrieving songs failed", Toast.LENGTH_SHORT).show()
            },
            onSuccess = { listOfSongs ->
//                Toast.makeText(applicationContext, "Loading songs", Toast.LENGTH_SHORT).show()
                success(listOfSongs as ArrayList<Song>)
            }
        ).execute(url)

    }

    /***                LISTS EDITION            ***/

    fun createList(listName : String, success : (Int) -> Unit ) {
        //Insert a new list in DB
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/listsongs.php?")
            .buildUpon()
            .appendQueryParameter("case", "1")
            .appendQueryParameter("list_name", listName)
            .appendQueryParameter("user_id", "1")
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
//                Toast.makeText(applicationContext, "Creating list failed", Toast.LENGTH_SHORT).show()
            },
            onSuccess = {listID ->
//                Toast.makeText(applicationContext, "Creating list successful", Toast.LENGTH_SHORT).show()
                success(listID as Int)
            }
        ).execute(url)
    }
    fun updateList(listID: Int, listName : String, success : (String) -> Unit ) {
        //Insert a new list in DB
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/listsongs.php?")
            .buildUpon()
            .appendQueryParameter("case", "2")
            .appendQueryParameter("list_id", listID.toString())
            .appendQueryParameter("list_name", listName)
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
//                Toast.makeText(applicationContext, "Creating list failed", Toast.LENGTH_SHORT).show()
            },
            onSuccess = {
//                Toast.makeText(applicationContext, "Creating list successful", Toast.LENGTH_SHORT).show()
                success(listName)
            }
        ).execute(url)
    }

    fun removeWholeList(listID:Int, success: (Boolean) -> Unit) {
        //Remove songsList and its relation  [two tables affected]
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/listsongs.php?")
            .buildUpon()
            .appendQueryParameter("case", "3")
            .appendQueryParameter("list_id", listID.toString())
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
//                Toast.makeText(applicationContext, "Removing list failed", Toast.LENGTH_SHORT).show()
            },
            onSuccess = {
                success(true)
            }
        ).execute(url)
    }

    fun insertToList(listID:Int, songsIDs:String) {
        //Insert songs into songsList
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/listsongs.php?")
            .buildUpon()
            .appendQueryParameter("case", "4")
            .appendQueryParameter("list_id", listID.toString())
            .appendQueryParameter("songs_ids", songsIDs)
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
//                Toast.makeText(applicationContext, "Adding into list failed", Toast.LENGTH_SHORT).show()
            },
            onSuccess = {
//                songsAdapter!!.notifyDataSetChanged()
            }
        ).execute(url)
    }

    fun removeFromList(listID:Int, songsIDs:String) {
        //Remove songs from songsList
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/listsongs.php?")
            .buildUpon()
            .appendQueryParameter("case", "5")
            .appendQueryParameter("list_id", listID.toString())
            .appendQueryParameter("songs_ids", songsIDs)
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
//                Toast.makeText(applicationContext, "Removing from list failed", Toast.LENGTH_SHORT).show()
            },
            onSuccess = {
//                songsAdapter!!.notifyDataSetChanged()
            }
        ).execute(url)
    }


}