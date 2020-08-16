package com.example.cancionerocatolico.api

import android.net.Uri
import com.example.cancionerocatolico.ViewMyListsActivity
import com.example.cancionerocatolico.objects.ListSongs
import com.example.cancionerocatolico.utils.MyAsyncTask
import com.example.cancionerocatolico.objects.Song
import com.example.cancionerocatolico.ViewSongsActivity
import com.example.cancionerocatolico.objects.User
import java.util.function.Supplier

open class CancioneroAPI(val userID : () -> Int) {
    val SERVER_URL = "https://abdf292d39d4.ngrok.io"
//    val SERVER_URL = "http://10.0.2.2:3000" //local


    /***************************************************                USERS                     ******************************************************************/
    fun loadUser(userEmail : String, success : (User) -> Unit, fail : (String) -> Unit){
        val url = Uri.parse("$SERVER_URL/users/?")
            .buildUpon()
            .appendQueryParameter("email", userEmail)
            .build()
            .toString()

        MyAsyncTask(
            onFail = { errorString ->
                fail(errorString)
            },
            onSuccess = { user ->
                success(user as User)
            }
        ).execute(url)
    }

    fun addUser(username: String, email: String, success: (Int) -> Unit, fail : (Any?) -> Unit) {
        //Update a song
        val url = Uri.parse("$SERVER_URL/users/add?")
            .buildUpon()
            .appendQueryParameter("username", username)
            .appendQueryParameter("email", email)
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
                fail(null)
            },
            onSuccess = { userID ->
                success(userID as Int)
            }
        ).execute(url)
    }

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
        val url = Uri.parse("$SERVER_URL/songs/?")
            .buildUpon()
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

    fun loadSongsByTags(tags : String, success : (ArrayList<Song>) -> Unit, fail : () -> Unit){
        //Search into DB based on tags
        val url = Uri.parse("$SERVER_URL/songs/tags?")
            .buildUpon()
            .appendQueryParameter("song_tags", tags)
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
                fail()
            },
            onSuccess = { listOfSongs ->
                success(listOfSongs as ArrayList<Song>)
            }
        ).execute(url)
    }

    /***                SONGS EDITION            ***/

    fun addSong(song: Song, success: (Int) -> Unit) {
        //Update a song
        val url = Uri.parse("$SERVER_URL/songs/create?")
            .buildUpon()
            .appendQueryParameter("song_title", song.songTitle)
            .appendQueryParameter("song_artist", song.songArtist)
            .appendQueryParameter("song_lyrics", song.songLyrics)
            .appendQueryParameter("song_tags", song.songTags)
            .build()
            .toString()

        MyAsyncTask(
            onFail = { },
            onSuccess = { songID ->
                success(songID as Int)
            }
        ).execute(url)
    }

    fun readSong(songID: Int, success: (Song) -> Unit) {
        //Reads a song
        val url = Uri.parse("$SERVER_URL/songs/song/?")
            .buildUpon()
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
        val url = Uri.parse("$SERVER_URL/songs/edit?")
            .buildUpon()
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
        val url = Uri.parse("$SERVER_URL/songs/delete?")
            .buildUpon()
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
        val url = Uri.parse("$SERVER_URL/listsongs/?")
            .buildUpon()
            .appendQueryParameter("user_id", userID.invoke().toString())
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
        val url = Uri.parse("$SERVER_URL/songs/list?")
            .buildUpon()
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
        val url = Uri.parse("$SERVER_URL/listsongs/create?")
            .buildUpon()
            .appendQueryParameter("list_name", listName)
            .appendQueryParameter("user_id", userID.invoke().toString())
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
        val url = Uri.parse("$SERVER_URL/listsongs/edit?")
            .buildUpon()
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
        val url = Uri.parse("$SERVER_URL/listsongs/delete?")
            .buildUpon()
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

    fun insertToList(listID:Int, songsIDs:String, success: () -> Unit) {
        //Insert songs into songsList
        val url = Uri.parse("$SERVER_URL/listsongs/insert?")
            .buildUpon()
            .appendQueryParameter("list_id", listID.toString())
            .appendQueryParameter("songs_ids", songsIDs)
            .build()
            .toString()

        MyAsyncTask(
            onFail = {
            },
            onSuccess = {
                success()
            }
        ).execute(url)
    }

    fun removeFromList(listID:Int, songsIDs:String, success: (Boolean) -> Unit) {
        //Remove songs from songsList
        val url = Uri.parse("$SERVER_URL/listsongs/remove?")
            .buildUpon()
            .appendQueryParameter("list_id", listID.toString())
            .appendQueryParameter("songs_ids", songsIDs)
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