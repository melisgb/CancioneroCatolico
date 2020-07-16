package com.example.cancionerocatolico.api

import android.net.Uri
import com.example.cancionerocatolico.ViewMyListsActivity
import com.example.cancionerocatolico.objects.ListSongs
import com.example.cancionerocatolico.utils.MyAsyncTask
import com.example.cancionerocatolico.objects.Song
import com.example.cancionerocatolico.ViewSongsActivity
import java.util.function.Supplier

open class CancioneroAPI(val userID : () -> String) {

    /***************************************************                SONGS                     ******************************************************************/
    fun loadSongs(){
        /*dummy data*/
        ViewSongsActivity.songsList.add(
            Song(
                1,
                "Un padre como el nuestro",
                "Entrenados",
                "Ula ula ula ula",
                "Entrada, Salmos"
            )
        )
        ViewSongsActivity.songsList.add(
            Song(
                2,
                "Solamente una vez",
                "Entrenados",
                "Ula ula ula ula",
                "Paz, Salmos"
            )
        )
        ViewSongsActivity.songsList.add(
            Song(
                3,
                "Una vez nada mas",
                "Entrenados",
                "Ula ula ula ula",
                "Paz, Salmos"
            )
        )
    }

    fun loadSongs(keyword : String, startFrom : Int, success : (ArrayList<Song>) -> Unit, fail : (Any?) -> Unit){
        //Search into DB based on keyword and updates the List
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/get_songs.php?")
            .buildUpon()
            .appendQueryParameter("case", "1")
            .appendQueryParameter("keyword", keyword)
            .appendQueryParameter("startFrom", startFrom.toString())
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
                fail(null)
            },
            onSuccess = { listOfSongs ->
                success(listOfSongs as ArrayList<Song>)
            }
        ).execute(url)
    }

    fun loadSongsByTags(tags : String, success : (ArrayList<Song>) -> Unit, fail : (Any?) -> Unit){
        //Search into DB based on tags
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/get_songs.php?")
            .buildUpon()
            .appendQueryParameter("case", "4")
            .appendQueryParameter("song_tags", tags)
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
            },
            onSuccess = { listOfSongs ->
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
            onFail = { },
            onSuccess = { song ->
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
            onFail = { },
            onSuccess = { listOfSongs ->
                val songs =
                    listOfSongs as ArrayList<Song>
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
            onFail = { },
            onSuccess = { songID ->
                success(null)
            }
        ).execute(url)
    }

    fun deleteSong( songID: Int, success: (Any?) -> Unit, fail: (Any?) -> Unit ) {
        //Deletes a song
        val url = Uri.parse("http://10.0.2.2:8000/cancionero/edit_song.php?")
            .buildUpon()
            .appendQueryParameter("case", "3")
            .appendQueryParameter("song_id", songID.toString())
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
                fail(null)
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
            .appendQueryParameter("user_id", userID.invoke())
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
            },
            onSuccess = { listsongs ->
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
            },
            onSuccess = { listOfSongs ->
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
            .appendQueryParameter("user_id", userID.invoke())
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
            },
            onSuccess = { listID ->
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
            },
            onSuccess = {
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
            },
            onSuccess = {
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
            },
            onSuccess = {
            }
        ).execute(url)
    }

    fun generateList(qty : Int) : ArrayList<ListSongs> {
        //dummy data
        var listOfListsSongs = ArrayList<ListSongs>()
        for(x in 1 until qty){
            val randSong = Song(
                100,
                "Un padre como el nuestro",
                "Entrenados",
                "Ula ula ula ula",
                "Entrada, Salmos"
            )
            var hashMap = HashMap<Int, Song>()
            hashMap.put(1, randSong)
            hashMap.put(2, randSong)
            hashMap.put(3, randSong)

            listOfListsSongs.add(
                ListSongs(
                    x,
                    "Favorites ${x}",
                    hashMap
                )
            )
        }
        return listOfListsSongs
    }


}