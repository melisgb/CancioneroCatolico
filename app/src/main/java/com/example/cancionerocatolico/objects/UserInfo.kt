package com.example.cancionerocatolico.objects

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.cancionerocatolico.LoginActivity

class UserInfo(context: Context) {
    var context : Context? = context
    var sharedRef : SharedPreferences? = null
    var userID = ""

    init {
        sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE)
    }
    fun saveUserInfo(userID : String){
        val editor = sharedRef!!.edit()
        editor.putString("userID", userID)
        editor.commit()
        loadUserInfo()
    }
    fun loadUserInfo(){
//        userID = "1"
        userID = sharedRef!!.getString("userID", "0").toString()

        if(userID == "0")
        { //start the login activity, otherwise not
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(intent)
        }
    }
}