package com.example.cancionerocatolico.utils

import android.os.AsyncTask
import android.util.Log
import com.example.cancionerocatolico.objects.ListSongs
import com.example.cancionerocatolico.objects.Song
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
            var inString = convertStreamToString(urlConnect.inputStream)
            //this function will publish the progress to the UI
            publishProgress(inString)
        } catch (ex: Exception) {
            Log.e("Exception error", ex.message, ex)
        }
        return " "
    }

    override fun onProgressUpdate(vararg values: String?) {
        try {
            var json = JSONObject(values[0])
            val msg = json.getString("msg")

            /****************************************      USER ACCESS           ********************************************/
            if(msg== "Register User is added"){
//                Log.d("UserRegistration", msg)
//                onSuccess(null)
            }
            else if(msg== "Login Successful"){
//                val msgInfo = JSONArray(json.getString("info"))
//                val userInfo = msgInfo.getJSONObject(0)
//                val user_id = userInfo.getString("user_id")
//                val username = userInfo.getString("user_name")
//                Log.d("UserLogin", username)
//                onSuccess(user_id)
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
                val listOfSongs = ArrayList<Song>()
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
            else if(msg== "Song updated"){ //For Edit Song - update
                Log.d("Song updated successfully", "")
                onSuccess(null)
            }
            else if(msg== "Song deleted"){ //For Edit Song - update
                Log.d("Song deleted successfully", "")
                onSuccess(null)
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
            else if(msg== "Updating listsongs - successful"){
                //TODO: check if neccesary to retrieve new listname
                Log.d("Updating listsong successful", "Successful")
                onSuccess(null)
            }
            else if(msg== "Deleting listsongs - successful"){
                Log.d("Deleting listsong successful", "Successful")
                onSuccess(null)
            }
            else if(msg== "Adding songs into listsongs - successful"){
                //TODO check if necessary to load the listsong
                Log.d("Adding songs into listsongs successful", "Successful")
                onSuccess(null)
            }
            else if(msg== "Removing songs from listsongs - successful"){
                //TODO check if necessary to load the listsong
                Log.d("Removing songs from listsongs successful", "Successful")
                onSuccess(null)
            }
            else {
                Log.d("Failed", msg)
                onFail()
            }
        } catch (ex: Exception) {
            Log.e("Exception error", ex.message, ex)
        }
    }
    override fun onPostExecute(result : String?) {
        super.onPostExecute(result)        //after the task is done
    }

    fun convertStreamToString(inputStrm: InputStream) : String{
        val bufferReader = BufferedReader(InputStreamReader(inputStrm))
        var line: String?
        var allString :String = ""

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