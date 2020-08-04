package com.example.cancionerocatolico.objects

import android.content.Context
import android.content.SharedPreferences

class UserInfo(context: Context) {
    //to save and return the UserID or username
    var context : Context? = context
    var sharedRef : SharedPreferences? = null
    init {
        sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE)
    }
    fun saveUserInfo(userID : Int, username : String){
        val editor = sharedRef!!.edit()
        editor.putInt("userID", userID)
        editor.putString("username", username)
        editor.commit()
        getUserID()
    }

    fun getUserID() : Int {
        val userID = sharedRef!!.getInt("userID", 0)
        return userID
    }
    fun getUserName() : String {
        val userName = sharedRef!!.getString("username", "noname").toString()
        return userName
    }
    fun clearUserInfo(){
        val editor = sharedRef!!.edit()
        editor.clear()
        editor.commit()
    }
}
