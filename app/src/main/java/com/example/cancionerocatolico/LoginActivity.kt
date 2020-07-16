package com.example.cancionerocatolico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.cancionerocatolico.objects.UserInfo

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = "Login"

        val loginBtn = findViewById<Button>(R.id.btnLogin)
        loginBtn.setOnClickListener {
            loginUser()
        }

    }
    fun loginUser(){
        //set if Firebase will be needed or just the DB, so far it will be manually
        val userID = 1.toString()
        val savedInfo =
            UserInfo(applicationContext)
        savedInfo.saveUserInfo(userID)
        Toast.makeText(applicationContext, "Login successful", Toast.LENGTH_SHORT).show()
        finish()
    }
}