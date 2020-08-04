package com.example.cancionerocatolico.utils

import android.os.AsyncTask
import android.util.Log
import com.example.cancionerocatolico.objects.ListSongs
import com.example.cancionerocatolico.objects.Song
import com.example.cancionerocatolico.objects.User
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

//To use as HTTP Request
class MyAsyncTask(val onSuccess: (Any?) -> Unit, val onFail: () -> Unit) : AsyncTask<String, String, String>() {

    override fun onPreExecute() {
        //before task started
        super.onPreExecute()
    }
    override fun doInBackground(vararg p0: String?): String {
        //http call
        try {
            val url = URL(p0[0])
            val urlConnect = url.openConnection() as HttpURLConnection
            urlConnect.connectTimeout = 5000

            if (urlConnect.responseCode != 200) {
                //400 = bad request - for parameters
                //401 = unauthorized
                //500 = server error
                publishProgress("DB Error")
            }
            else {
            //Responsecode 200
                var inString = convertStreamToString(urlConnect.inputStream)
                //this function will publish the progress to the UI
                publishProgress(inString)
            }
        } catch (ex: Exception) {
            Log.e("Exception error", ex.message, ex)
        }
        return " "
    }

    override fun onProgressUpdate(vararg values: String?) {
        if(values[0].equals("DB Error")) {
            //TODO: Retrieve the response message from DB error
            Log.d("Failed", "Database Error")
            onFail()
            return
        }
        try {
            val json = JSONObject(values[0])
            val msg = json.getString("msg")

            /****************************************      USER ACCESS           ********************************************/
            if(msg== "User created - successful"){
                val newUserID = json.getInt("userID")
                Log.d("User created successfully","New ID $newUserID")
                onSuccess(newUserID)
            }
            else if(msg== "Loading user - successful"){
                val userInfo = JSONArray(json.getString("userInfo")).getJSONObject(0)
                val newUser = User(
                    userInfo.getInt("user_id"),
                    userInfo.getString("user_name"),
                    userInfo.getString("user_email")
                )
                Log.d("UserLogin", newUser.username)
                onSuccess(newUser)
            }
            /****************************************         SONGS           ********************************************/
            else if(msg== "Loading songs - successful"){
                val songsInfoArr = JSONArray(json.getString("songsInfo"))
                val listOfSongs = ArrayList<Song>()
                for(i in 0 until songsInfoArr.length()){
                    val songInfo = songsInfoArr.getJSONObject(i)
                    listOfSongs.add(
                        Song(
                            songInfo.getInt("song_id"),
                            songInfo.getString("song_title"),
                            songInfo.getString("song_artist"),
                            songInfo.getString("song_lyrics"),
                            songInfo.getString("song_tags")
                        )
                    )
                }
                Log.d("Songs loaded successful", "Qty songs: ${listOfSongs.size}")
                onSuccess(listOfSongs)
            }
            else if(msg== "Song saved"){ //For Edit Song - add
                val songInfoArr = JSONArray(json.getString("songInfo"))
                val songInfo = songInfoArr.getJSONObject(0)
                val newSong = Song(
                    songInfo.getInt("song_id"),
                    songInfo.getString("song_title"),
                    songInfo.getString("song_artist"),
                    songInfo.getString("song_lyrics"),
                    songInfo.getString("song_tags")
                )

                Log.d("Song saved successful", "")
                onSuccess(newSong)
            }

            /****************************************         LISTS           ********************************************/
            else if(msg== "Loading summary lists - successful"){
                val listsInfoArr = JSONArray(json.getString("listsongs"))
                val listOfLists = ArrayList<ListSongs>()
                for(i in 0 until listsInfoArr.length()){
                    val listsongInfo = listsInfoArr.getJSONObject(i)
                    listOfLists.add(
                        ListSongs(
                            listsongInfo.getInt("listsong_id"),
                            listsongInfo.getString("listsong_name"),
                            HashMap<Int, Song>()
                        )
                    )
                }
                Log.d("Summary lists loaded successful", "Qty lists: ${listOfLists.size}")
                onSuccess(listOfLists)
            }
            else if(msg== "Adding listsongs - successful"){
                val listID = json.getInt("listsongID")
                Log.d("Creating listsong successful", "Successful")
                onSuccess(listID)
            }

            else{
                /*
                this response will group all the OK responses that wont retrieve information, such as:
                    Song updated   --> SONGS
                    Song deleted   --> SONGS
                    Updating listsongs - successful   --> LISTSONGS
                    Deleting listsongs - successful   --> LISTSONGS
                    Adding songs into listsongs - successful   --> LISTSONGS
                    Removing songs from listsongs - successful   --> LISTSONGS
                */
                Log.d(msg.toString(), "")
                onSuccess(null)
            }
        } catch (ex: Exception) {
            Log.e("Exception error", ex.message, ex)
        }
    }
    override fun onPostExecute(result : String?) {
        super.onPostExecute(result)        //after the task is done
    }

    private fun convertStreamToString(inputStrm: InputStream) : String{
        val bufferReader = BufferedReader(InputStreamReader(inputStrm))
        var line: String?
        var allString  = ""

        try{
            do {
                line = bufferReader.readLine()
                if(line!=null){
                    allString+=line
                }
            }
            while(line != null)
            inputStrm.close()
        }catch (ex:Exception){
            Log.e("Exception error", ex.toString())
        }
        return allString
    }
}