package com.gmgb.cancionerocatolico.utils

import android.content.Context
import com.gmgb.cancionerocatolico.objects.UserInfo

class UserHelper {
    //accessible from every activity
    companion object{
        fun getUserID(context: Context) : Int {
            val savedInfo = UserInfo(context)
            return savedInfo.getUserID()
        }
        fun getUserName(context: Context) : String {
            val savedInfo = UserInfo(context)
            return savedInfo.getUserName()
        }
    }
}