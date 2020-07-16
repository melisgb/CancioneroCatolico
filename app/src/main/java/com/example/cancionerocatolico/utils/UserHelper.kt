package com.example.cancionerocatolico.utils

import android.content.Context
import com.example.cancionerocatolico.objects.UserInfo

class UserHelper {
    companion object{
        fun getUserID(context: Context) : String {
            val savedInfo = UserInfo(context)
            savedInfo.loadUserInfo()
            return savedInfo.sharedRef!!.getString("userID", "0").toString()
        }
    }
}