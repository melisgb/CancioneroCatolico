package com.example.cancionerocatolico.utils

import android.content.Context
import com.example.cancionerocatolico.objects.UserInfo

class UserHelper {
    companion object{
        fun getUserID(context: Context) : Int {
            val savedInfo = UserInfo(context)
            savedInfo.getUserID()
            return savedInfo.sharedRef!!.getInt("userID", 0)
        }
        fun getUserName(context: Context) : String {
            val savedInfo = UserInfo(context)
            savedInfo.getUserID()
            return savedInfo.sharedRef!!.getString("username", "noname").toString()
        }
    }
}